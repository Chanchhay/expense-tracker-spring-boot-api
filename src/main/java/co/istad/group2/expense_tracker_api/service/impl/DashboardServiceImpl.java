package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.Transaction;
import co.istad.group2.expense_tracker_api.domain.TransactionImage;
import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.domain.enums.TransactionType;
import co.istad.group2.expense_tracker_api.dto.response.dashboardResponse.DashboardCurrencyTotalResponse;
import co.istad.group2.expense_tracker_api.dto.response.dashboardResponse.DashboardResponse;
import co.istad.group2.expense_tracker_api.dto.response.transactionResponse.TransactionImageResponse;
import co.istad.group2.expense_tracker_api.dto.response.transactionResponse.TransactionResponse;
import co.istad.group2.expense_tracker_api.repository.TransactionRepository;
import co.istad.group2.expense_tracker_api.repository.UserRepository;
import co.istad.group2.expense_tracker_api.service.DashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public DashboardServiceImpl(UserRepository userRepository,
                                TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public DashboardResponse getDashboardSummary(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        LocalDate endDate = LocalDate.now().plusDays(1);
        LocalDate startDate = LocalDate.now().minusDays(29);

        List<Transaction> last30DaysTransactions =
                transactionRepository.findByUserAndDateGreaterThanEqualAndDateLessThanOrderByDateAscCreatedAtAsc(
                        user,
                        startDate,
                        endDate
                );

        Map<String, BigDecimal> incomeByCurrency = new LinkedHashMap<>();
        Map<String, BigDecimal> expenseByCurrency = new LinkedHashMap<>();

        for (Transaction transaction : last30DaysTransactions) {
            String currency = transaction.getCurrency();

            if (transaction.getType() == TransactionType.INCOME) {
                incomeByCurrency.merge(currency, transaction.getAmount(), BigDecimal::add);
            } else {
                expenseByCurrency.merge(currency, transaction.getAmount(), BigDecimal::add);
            }
        }

        Map<String, Boolean> currencies = new LinkedHashMap<>();
        incomeByCurrency.keySet().forEach(currency -> currencies.put(currency, true));
        expenseByCurrency.keySet().forEach(currency -> currencies.put(currency, true));

        List<DashboardCurrencyTotalResponse> totalsByCurrency = currencies.keySet()
                .stream()
                .map(currency -> {
                    BigDecimal incomeLast30Days = incomeByCurrency.getOrDefault(currency, BigDecimal.ZERO);
                    BigDecimal expenseLast30Days = expenseByCurrency.getOrDefault(currency, BigDecimal.ZERO);
                    BigDecimal netCashFlowLast30Days = incomeLast30Days.subtract(expenseLast30Days);

                    return DashboardCurrencyTotalResponse.builder()
                            .currency(currency)
                            .incomeLast30Days(incomeLast30Days)
                            .expenseLast30Days(expenseLast30Days)
                            .netCashFlowLast30Days(netCashFlowLast30Days)
                            .build();
                })
                .toList();

        List<TransactionResponse> recentTransactions = transactionRepository
                .findTop5ByUserOrderByDateDescCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();

        return DashboardResponse.builder()
                .totalsByCurrency(totalsByCurrency)
                .recentTransactions(recentTransactions)
                .build();
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .accountId(transaction.getAccount().getId())
                .accountName(transaction.getAccount().getName())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .currency(transaction.getCurrency())
                .date(transaction.getDate())
                .note(transaction.getNote())
                .source(transaction.getSource())
                .images(mapTransactionImages(transaction.getImages()))
                .build();
    }


    private List<TransactionImageResponse> mapTransactionImages(List<TransactionImage> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }

        return images.stream()
                .map(image -> TransactionImageResponse.builder()
                        .id(image.getId())
                        .imageUrl(image.getImageUrl())
                        .imagePublicId(image.getImagePublicId())
                        .build())
                .toList();
    }
}