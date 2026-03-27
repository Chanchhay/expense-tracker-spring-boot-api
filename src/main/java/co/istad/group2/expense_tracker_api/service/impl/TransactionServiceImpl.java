package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.Category;
import co.istad.group2.expense_tracker_api.domain.Transaction;
import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.domain.enums.TransactionType;
import co.istad.group2.expense_tracker_api.dto.request.CreateTransactionRequest;
import co.istad.group2.expense_tracker_api.dto.request.UpdateTransactionRequest;
import co.istad.group2.expense_tracker_api.dto.response.TransactionResponse;
import co.istad.group2.expense_tracker_api.exception.NotFoundException;
import co.istad.group2.expense_tracker_api.repository.CategoryRepository;
import co.istad.group2.expense_tracker_api.repository.TransactionRepository;
import co.istad.group2.expense_tracker_api.repository.UserRepository;
import co.istad.group2.expense_tracker_api.service.TransactionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public TransactionResponse createTransaction(CreateTransactionRequest request, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        Category category = categoryRepository.findByIdAndUser(request.categoryId(), user).orElseThrow(() -> new NotFoundException("Category not found or does not belong to user"));

        Transaction transaction = new Transaction();
        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setCurrency(request.currency().trim().toUpperCase());
        transaction.setDate(request.date());
        transaction.setNote(request.note() == null ? null : request.note().trim());
        transaction.setUser(user);
        transaction.setCategory(category);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToResponse(savedTransaction);
    }

    @Override
    public List<TransactionResponse> getMyTransactions(String email, TransactionType type, String month) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        List<Transaction> transactions;

        if (month != null && !month.isBlank()) {
            YearMonth yearMonth = YearMonth.parse(month); // format: 2026-03
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.plusMonths(1).atDay(1);

            if (type != null) {
                transactions = transactionRepository.findByUserAndTypeAndDateGreaterThanEqualAndDateLessThanOrderByDateDescCreatedAtDesc(user, type, startDate, endDate);
            } else {
                transactions = transactionRepository.findByUserAndDateGreaterThanEqualAndDateLessThanOrderByDateDescCreatedAtDesc(user, startDate, endDate);
            }
        } else {
            if (type != null) {
                transactions = transactionRepository.findByUserAndTypeOrderByDateDescCreatedAtDesc(user, type);
            } else {
                transactions = transactionRepository.findByUserOrderByDateDescCreatedAtDesc(user);
            }
        }

        return transactions.stream().map(this::mapToResponse).toList();
    }

    @Override
    public TransactionResponse updateTransaction(String id, UpdateTransactionRequest request, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        Transaction transaction = transactionRepository.findByIdAndUser(id, user).orElseThrow(() -> new NotFoundException("Transaction not found or does not belong to user"));

        Category category = categoryRepository.findByIdAndUser(request.categoryId(), user).orElseThrow(() -> new NotFoundException("Category not found or does not belong to user"));

        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setCurrency(request.currency().trim().toUpperCase());
        transaction.setDate(request.date());
        transaction.setNote(request.note() == null ? null : request.note().trim());
        transaction.setCategory(category);

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return mapToResponse(updatedTransaction);
    }

    @Override
    public void deleteTransaction(String id, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        Transaction transaction = transactionRepository.findByIdAndUser(id, user).orElseThrow(() -> new NotFoundException("Transaction not found or does not belong to user"));

        transactionRepository.delete(transaction);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder().id(transaction.getId()).amount(transaction.getAmount()).type(transaction.getType()).categoryId(transaction.getCategory().getId()).categoryName(transaction.getCategory().getName()).currency(transaction.getCurrency()).date(transaction.getDate()).note(transaction.getNote()).build();
    }
}