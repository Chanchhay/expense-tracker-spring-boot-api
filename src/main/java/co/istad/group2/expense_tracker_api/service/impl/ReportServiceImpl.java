package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.Transaction;
import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.domain.enums.TransactionType;
import co.istad.group2.expense_tracker_api.dto.response.accountResponse.AccountSummaryItemResponse;
import co.istad.group2.expense_tracker_api.dto.response.accountResponse.AccountSummaryResponse;
import co.istad.group2.expense_tracker_api.dto.response.accountResponse.CashFlowCurrencyGroupResponse;
import co.istad.group2.expense_tracker_api.dto.response.accountResponse.CurrencyBalanceTotalResponse;
import co.istad.group2.expense_tracker_api.dto.response.categoryResponse.CategoryBreakdownCurrencyGroupResponse;
import co.istad.group2.expense_tracker_api.dto.response.categoryResponse.CategoryBreakdownItemResponse;
import co.istad.group2.expense_tracker_api.dto.response.categoryResponse.CategoryBreakdownResponse;
import co.istad.group2.expense_tracker_api.dto.response.dashboardResponse.MonthlySummaryCurrencyGroupResponse;
import co.istad.group2.expense_tracker_api.dto.response.dashboardResponse.MonthlySummaryResponse;
import co.istad.group2.expense_tracker_api.dto.response.dashboardResponse.TopExpenseItemResponse;
import co.istad.group2.expense_tracker_api.dto.response.dashboardResponse.TopExpensesResponse;
import co.istad.group2.expense_tracker_api.dto.response.goalsResponse.CashFlowItemResponse;
import co.istad.group2.expense_tracker_api.dto.response.goalsResponse.CashFlowResponse;
import co.istad.group2.expense_tracker_api.exception.BadRequestException;
import co.istad.group2.expense_tracker_api.exception.NotFoundException;
import co.istad.group2.expense_tracker_api.repository.AccountRepository;
import co.istad.group2.expense_tracker_api.repository.TransactionRepository;
import co.istad.group2.expense_tracker_api.repository.UserRepository;
import co.istad.group2.expense_tracker_api.service.ReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public ReportServiceImpl(UserRepository userRepository,
                             TransactionRepository transactionRepository,
                             AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public MonthlySummaryResponse getMonthlySummary(String email, String month) {
        User user = getUserByEmail(email);

        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.plusMonths(1).atDay(1);

        List<Transaction> transactions = transactionRepository
                .findByUserAndDateGreaterThanEqualAndDateLessThanOrderByDateAscCreatedAtAsc(
                        user, startDate, endDate
                );

        Map<String, BigDecimal> incomeMap = new LinkedHashMap<>();
        Map<String, BigDecimal> expenseMap = new LinkedHashMap<>();

        for (Transaction t : transactions) {
            String currency = t.getCurrency();

            if (t.getType() == TransactionType.INCOME) {
                incomeMap.merge(currency, t.getAmount(), BigDecimal::add);
            } else {
                expenseMap.merge(currency, t.getAmount(), BigDecimal::add);
            }
        }

        Map<String, Boolean> currencies = new LinkedHashMap<>();
        incomeMap.keySet().forEach(c -> currencies.put(c, true));
        expenseMap.keySet().forEach(c -> currencies.put(c, true));

        List<MonthlySummaryCurrencyGroupResponse> groups = currencies.keySet()
                .stream()
                .map(currency -> {
                    BigDecimal income = incomeMap.getOrDefault(currency, BigDecimal.ZERO);
                    BigDecimal expense = expenseMap.getOrDefault(currency, BigDecimal.ZERO);
                    BigDecimal net = income.subtract(expense);

                    return MonthlySummaryCurrencyGroupResponse.builder()
                            .currency(currency)
                            .totalIncome(income)
                            .totalExpense(expense)
                            .netBalance(net)
                            .build();
                })
                .toList();

        return MonthlySummaryResponse.builder()
                .month(month)
                .groups(groups)
                .build();
    }

    @Override
    public CategoryBreakdownResponse getCategoryBreakdown(String email, String month) {
        User user = getUserByEmail(email);

        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.plusMonths(1).atDay(1);

        List<Transaction> transactions = transactionRepository
                .findByUserAndDateGreaterThanEqualAndDateLessThanOrderByDateAscCreatedAtAsc(
                        user, startDate, endDate
                )
                .stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .toList();

        Map<String, List<Transaction>> transactionsByCurrency = new LinkedHashMap<>();

        for (Transaction t : transactions) {
            transactionsByCurrency
                    .computeIfAbsent(t.getCurrency(), k -> new ArrayList<>())
                    .add(t);
        }

        List<CategoryBreakdownCurrencyGroupResponse> groups = new ArrayList<>();

        for (Map.Entry<String, List<Transaction>> entry : transactionsByCurrency.entrySet()) {

            String currency = entry.getKey();
            List<Transaction> currencyTransactions = entry.getValue();

            BigDecimal totalExpense = currencyTransactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<Integer, BigDecimal> categoryAmounts = new LinkedHashMap<>();
            Map<Integer, String> categoryNames = new LinkedHashMap<>();

            for (Transaction t : currencyTransactions) {
                Integer categoryId = t.getCategory().getId();

                categoryNames.put(categoryId, t.getCategory().getName());
                categoryAmounts.merge(categoryId, t.getAmount(), BigDecimal::add);
            }

            List<CategoryBreakdownItemResponse> items = categoryAmounts.entrySet()
                    .stream()
                    .map(e -> {
                        BigDecimal percentage = BigDecimal.ZERO;

                        if (totalExpense.compareTo(BigDecimal.ZERO) > 0) {
                            percentage = e.getValue()
                                    .multiply(BigDecimal.valueOf(100))
                                    .divide(totalExpense, 2, RoundingMode.HALF_UP);
                        }

                        return CategoryBreakdownItemResponse.builder()
                                .categoryId(e.getKey())
                                .categoryName(categoryNames.get(e.getKey()))
                                .amount(e.getValue())
                                .percentage(percentage)
                                .build();
                    })
                    .sorted(Comparator.comparing(CategoryBreakdownItemResponse::amount).reversed())
                    .toList();

            groups.add(
                    CategoryBreakdownCurrencyGroupResponse.builder()
                            .currency(currency)
                            .totalExpense(totalExpense)
                            .items(items)
                            .build()
            );
        }

        return CategoryBreakdownResponse.builder()
                .month(month)
                .groups(groups)
                .build();
    }

    @Override
    public AccountSummaryResponse getAccountSummary(String email) {
        User user = getUserByEmail(email);

        List<AccountSummaryItemResponse> items = accountRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(account -> AccountSummaryItemResponse.builder()
                        .accountId(account.getId())
                        .accountName(account.getName())
                        .accountType(account.getType())
                        .currency(account.getCurrency())
                        .initialBalance(account.getInitialBalance())
                        .currentBalance(account.getCurrentBalance())
                        .build())
                .toList();

        Map<String, BigDecimal> totalsMap = new LinkedHashMap<>();

        for (AccountSummaryItemResponse item : items) {
            totalsMap.merge(
                    item.currency(),
                    item.currentBalance(),
                    BigDecimal::add
            );
        }

        List<CurrencyBalanceTotalResponse> totalsByCurrency = totalsMap.entrySet()
                .stream()
                .map(entry -> CurrencyBalanceTotalResponse.builder()
                        .currency(entry.getKey())
                        .totalBalance(entry.getValue())
                        .build())
                .toList();

        return AccountSummaryResponse.builder()
                .totalsByCurrency(totalsByCurrency)
                .items(items)
                .build();
    }

    @Override
    public CashFlowResponse getCashFlow(String email, LocalDate from, LocalDate to, String groupBy) {
        User user = getUserByEmail(email);

        if (from == null || to == null) {
            throw new BadRequestException("'from' and 'to' are required");
        }

        if (to.isBefore(from)) {
            throw new BadRequestException("'to' cannot be before 'from'");
        }

        String normalizedGroupBy = groupBy == null ? "DAY" : groupBy.trim().toUpperCase();
        if (!normalizedGroupBy.equals("DAY") && !normalizedGroupBy.equals("MONTH")) {
            throw new BadRequestException("groupBy must be either DAY or MONTH");
        }

        List<Transaction> transactions = transactionRepository
                .findByUserAndDateGreaterThanEqualAndDateLessThanOrderByDateAscCreatedAtAsc(
                        user,
                        from,
                        to.plusDays(1)
                );

        DateTimeFormatter formatter = normalizedGroupBy.equals("MONTH")
                ? DateTimeFormatter.ofPattern("yyyy-MM")
                : DateTimeFormatter.ISO_DATE;

        Map<String, Map<String, BigDecimal>> incomeByCurrency = new LinkedHashMap<>();
        Map<String, Map<String, BigDecimal>> expenseByCurrency = new LinkedHashMap<>();

        for (Transaction transaction : transactions) {

            String currency = transaction.getCurrency();

            String key = normalizedGroupBy.equals("MONTH")
                    ? YearMonth.from(transaction.getDate()).format(formatter)
                    : transaction.getDate().format(formatter);

            incomeByCurrency.putIfAbsent(currency, new LinkedHashMap<>());
            expenseByCurrency.putIfAbsent(currency, new LinkedHashMap<>());

            if (transaction.getType() == TransactionType.INCOME) {
                incomeByCurrency.get(currency).merge(key, transaction.getAmount(), BigDecimal::add);
            } else {
                expenseByCurrency.get(currency).merge(key, transaction.getAmount(), BigDecimal::add);
            }
        }

        List<CashFlowCurrencyGroupResponse> groups = new ArrayList<>();

        for (String currency : incomeByCurrency.keySet()) {

            Map<String, BigDecimal> incomeMap = incomeByCurrency.getOrDefault(currency, new LinkedHashMap<>());
            Map<String, BigDecimal> expenseMap = expenseByCurrency.getOrDefault(currency, new LinkedHashMap<>());

            Map<String, Boolean> allPeriods = new LinkedHashMap<>();
            incomeMap.keySet().forEach(p -> allPeriods.put(p, true));
            expenseMap.keySet().forEach(p -> allPeriods.put(p, true));

            List<CashFlowItemResponse> items = allPeriods.keySet()
                    .stream()
                    .sorted()
                    .map(period -> {
                        BigDecimal income = incomeMap.getOrDefault(period, BigDecimal.ZERO);
                        BigDecimal expense = expenseMap.getOrDefault(period, BigDecimal.ZERO);
                        BigDecimal net = income.subtract(expense);

                        return CashFlowItemResponse.builder()
                                .period(period)
                                .income(income)
                                .expense(expense)
                                .net(net)
                                .build();
                    })
                    .toList();

            groups.add(
                    CashFlowCurrencyGroupResponse.builder()
                            .currency(currency)
                            .items(items)
                            .build()
            );
        }
        return CashFlowResponse.builder()
                .from(from)
                .to(to)
                .groupBy(normalizedGroupBy)
                .groups(groups)
                .build();
    }

    @Override
    public TopExpensesResponse getTopExpenses(String email, String month, Integer limit) {
        User user = getUserByEmail(email);

        int safeLimit = (limit == null || limit <= 0) ? 5 : limit;

        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.plusMonths(1).atDay(1);

        List<TopExpenseItemResponse> items = transactionRepository
                .findByUserAndDateGreaterThanEqualAndDateLessThanOrderByDateAscCreatedAtAsc(
                        user, startDate, endDate
                )
                .stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .limit(safeLimit)
                .map(transaction -> TopExpenseItemResponse.builder()
                        .transactionId(transaction.getId())
                        .accountId(transaction.getAccount().getId())
                        .accountName(transaction.getAccount().getName())
                        .categoryId(transaction.getCategory().getId())
                        .categoryName(transaction.getCategory().getName())
                        .amount(transaction.getAmount())
                        .currency(transaction.getCurrency())
                        .date(transaction.getDate())
                        .note(transaction.getNote())
                        .build())
                .toList();

        return TopExpensesResponse.builder()
                .month(month)
                .limit(safeLimit)
                .items(items)
                .build();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    private BigDecimal sumByType(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}