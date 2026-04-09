package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.Transaction;
import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.domain.enums.TransactionType;
import co.istad.group2.expense_tracker_api.dto.response.accountResponse.AccountSummaryItemResponse;
import co.istad.group2.expense_tracker_api.dto.response.accountResponse.AccountSummaryResponse;
import co.istad.group2.expense_tracker_api.dto.response.categoryResponse.CategoryBreakdownItemResponse;
import co.istad.group2.expense_tracker_api.dto.response.categoryResponse.CategoryBreakdownResponse;
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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        BigDecimal totalIncome = sumByType(transactions, TransactionType.INCOME);
        BigDecimal totalExpense = sumByType(transactions, TransactionType.EXPENSE);
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        return MonthlySummaryResponse.builder()
                .month(month)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(netBalance)
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

        BigDecimal totalExpense = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Integer, BigDecimal> categoryAmounts = new LinkedHashMap<>();
        Map<Integer, String> categoryNames = new LinkedHashMap<>();

        for (Transaction transaction : transactions) {
            Integer categoryId = transaction.getCategory().getId();
            categoryNames.put(categoryId, transaction.getCategory().getName());
            categoryAmounts.merge(categoryId, transaction.getAmount(), BigDecimal::add);
        }

        List<CategoryBreakdownItemResponse> items = categoryAmounts.entrySet()
                .stream()
                .map(entry -> {
                    BigDecimal percentage = BigDecimal.ZERO;

                    if (totalExpense.compareTo(BigDecimal.ZERO) > 0) {
                        percentage = entry.getValue()
                                .multiply(BigDecimal.valueOf(100))
                                .divide(totalExpense, 2, RoundingMode.HALF_UP);
                    }

                    return CategoryBreakdownItemResponse.builder()
                            .categoryId(entry.getKey())
                            .categoryName(categoryNames.get(entry.getKey()))
                            .amount(entry.getValue())
                            .percentage(percentage)
                            .build();
                })
                .sorted(Comparator.comparing(CategoryBreakdownItemResponse::amount).reversed())
                .toList();

        return CategoryBreakdownResponse.builder()
                .month(month)
                .totalExpense(totalExpense)
                .items(items)
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

        BigDecimal totalCurrentBalance = items.stream()
                .map(AccountSummaryItemResponse::currentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AccountSummaryResponse.builder()
                .totalCurrentBalance(totalCurrentBalance)
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

        Map<String, BigDecimal> incomeMap = new LinkedHashMap<>();
        Map<String, BigDecimal> expenseMap = new LinkedHashMap<>();

        DateTimeFormatter formatter = normalizedGroupBy.equals("MONTH")
                ? DateTimeFormatter.ofPattern("yyyy-MM")
                : DateTimeFormatter.ISO_DATE;

        for (Transaction transaction : transactions) {
            String key = normalizedGroupBy.equals("MONTH")
                    ? YearMonth.from(transaction.getDate()).format(formatter)
                    : transaction.getDate().format(formatter);

            if (transaction.getType() == TransactionType.INCOME) {
                incomeMap.merge(key, transaction.getAmount(), BigDecimal::add);
            } else {
                expenseMap.merge(key, transaction.getAmount(), BigDecimal::add);
            }
        }

        Map<String, Boolean> allPeriods = new LinkedHashMap<>();
        incomeMap.keySet().forEach(period -> allPeriods.put(period, true));
        expenseMap.keySet().forEach(period -> allPeriods.put(period, true));

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

        return CashFlowResponse.builder()
                .from(from)
                .to(to)
                .groupBy(normalizedGroupBy)
                .items(items)
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