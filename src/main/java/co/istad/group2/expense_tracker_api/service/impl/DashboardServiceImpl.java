package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.Transaction;
import co.istad.group2.expense_tracker_api.domain.TransactionImage;
import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.domain.enums.TransactionType;
import co.istad.group2.expense_tracker_api.dto.response.dashboardResponse.DashboardResponse;
import co.istad.group2.expense_tracker_api.dto.response.transactionResponse.TransactionImageResponse;
import co.istad.group2.expense_tracker_api.dto.response.transactionResponse.TransactionResponse;
import co.istad.group2.expense_tracker_api.repository.TransactionRepository;
import co.istad.group2.expense_tracker_api.repository.UserRepository;
import co.istad.group2.expense_tracker_api.service.DashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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

        BigDecimal totalIncome = transactionRepository.sumAmountByUserAndType(user, TransactionType.INCOME);
        BigDecimal totalExpense = transactionRepository.sumAmountByUserAndType(user, TransactionType.EXPENSE);
        BigDecimal currentBalance = totalIncome.subtract(totalExpense);

        List<TransactionResponse> recentTransactions = transactionRepository
                .findTop5ByUserOrderByDateDescCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();

        return DashboardResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .currentBalance(currentBalance)
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