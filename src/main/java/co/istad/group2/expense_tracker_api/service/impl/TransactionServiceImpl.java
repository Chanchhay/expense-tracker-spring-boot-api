package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.*;
import co.istad.group2.expense_tracker_api.domain.enums.TransactionType;
import co.istad.group2.expense_tracker_api.dto.request.createReq.CreateTransactionRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateTransactionRequest;
import co.istad.group2.expense_tracker_api.dto.response.transactionResponse.TransactionImageResponse;
import co.istad.group2.expense_tracker_api.dto.response.transactionResponse.TransactionResponse;
import co.istad.group2.expense_tracker_api.exception.BadRequestException;
import co.istad.group2.expense_tracker_api.exception.NotFoundException;
import co.istad.group2.expense_tracker_api.repository.AccountRepository;
import co.istad.group2.expense_tracker_api.repository.CategoryRepository;
import co.istad.group2.expense_tracker_api.repository.TransactionRepository;
import co.istad.group2.expense_tracker_api.repository.UserRepository;
import co.istad.group2.expense_tracker_api.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  UserRepository userRepository,
                                  CategoryRepository categoryRepository,
                                  AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public TransactionResponse createTransaction(CreateTransactionRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        Category category = categoryRepository.findByIdAndUserOrUserIdIsNullAndId(request.categoryId(), user, request.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found or does not belong to user"));

        Account account = accountRepository.findByIdAndUser(request.accountId(), user)
                .orElseThrow(() -> new NotFoundException("Account not found or does not belong to user"));

        if (request.type().equals(TransactionType.valueOf("EXPENSE")) && request.amount().compareTo(account.getCurrentBalance()) > 0) {
            throw new BadRequestException("Insufficient amount Expense amount: " + request.amount() + " Cannot be bigger than Current balance: " + account.getCurrentBalance());
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setCurrency(account.getCurrency().toUpperCase());
        transaction.setDate(request.date());
        transaction.setNote(request.note() == null ? null : request.note().trim().replaceAll("\\s+", " "));
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setSource(request.source().trim().replaceAll("\\s+", " "));
        if (request.images() != null) {
            request.images().forEach(imageRequest -> {
                TransactionImage image = new TransactionImage();
                image.setImageUrl(imageRequest.imageUrl());
                image.setImagePublicId(imageRequest.imagePublicId());
                transaction.addImage(image);
            });
        }
        applyTransactionEffect(account, transaction.getType(), transaction.getAmount());

        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToResponse(savedTransaction);
    }

    @Override
    public List<TransactionResponse> getMyTransactions(String email,
                                                       TransactionType type,
                                                       String month) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        List<Transaction> transactions;

        if (month != null && !month.isBlank()) {
            YearMonth yearMonth = YearMonth.parse(month);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.plusMonths(1).atDay(1);

            if (type != null) {
                transactions = transactionRepository
                        .findByUserAndTypeAndDateGreaterThanEqualAndDateLessThanOrderByDateDescCreatedAtDesc(
                                user, type, startDate, endDate
                        );
            } else {
                transactions = transactionRepository
                        .findByUserAndDateGreaterThanEqualAndDateLessThanOrderByDateDescCreatedAtDesc(
                                user, startDate, endDate
                        );
            }
        } else {
            if (type != null) {
                transactions = transactionRepository
                        .findByUserAndTypeOrderByDateDescCreatedAtDesc(user, type);
            } else {
                transactions = transactionRepository
                        .findByUserOrderByDateDescCreatedAtDesc(user);
            }
        }

        return transactions.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public TransactionResponse updateTransaction(String id,
                                                 UpdateTransactionRequest request,
                                                 String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Transaction not found or does not belong to user"));

        Category category = categoryRepository.findByIdAndUserOrUserIdIsNullAndId(request.categoryId(), user, request.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found or does not belong to user"));

        Account newAccount = accountRepository.findByIdAndUser(request.accountId(), user)
                .orElseThrow(() -> new NotFoundException("Account not found or does not belong to user"));

        Account oldAccount = transaction.getAccount();

        reverseTransactionEffect(oldAccount, transaction.getType(), transaction.getAmount());

        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setCurrency(newAccount.getCurrency().toUpperCase());
        transaction.setDate(request.date());
        transaction.setNote(request.note() == null ? null : request.note().trim().replaceAll("\\s+", " "));
        transaction.setCategory(category);
        transaction.setAccount(newAccount);
        String payee = request.source().trim().replaceAll("\\s+", " ");
        transaction.setSource(payee);
        if (request.images() != null) {
            request.images().forEach(imageRequest -> {
                TransactionImage image = new TransactionImage();
                image.setImageUrl(imageRequest.imageUrl());
                image.setImagePublicId(imageRequest.imagePublicId());
                transaction.addImage(image);
            });
        }

        applyTransactionEffect(newAccount, transaction.getType(), transaction.getAmount());

        if (request.type().equals(TransactionType.valueOf("EXPENSE")) && request.amount().compareTo(newAccount.getCurrentBalance()) > 0) {
            throw new BadRequestException("Insufficient amount Expense amount Cannot be bigger than Current balance");
        }

        accountRepository.save(oldAccount);
        accountRepository.save(newAccount);

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return mapToResponse(updatedTransaction);
    }

    @Override
    public void deleteTransaction(String id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Transaction not found or does not belong to user"));

        Account account = transaction.getAccount();
        reverseTransactionEffect(account, transaction.getType(), transaction.getAmount());

        accountRepository.save(account);
        transactionRepository.delete(transaction);
    }

    private void applyTransactionEffect(Account account, TransactionType type, BigDecimal amount) {
        if (type == TransactionType.INCOME) {
            account.setCurrentBalance(account.getCurrentBalance().add(amount));
        } else {
            account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
        }
    }

    private void reverseTransactionEffect(Account account, TransactionType type, BigDecimal amount) {
        if (type == TransactionType.INCOME) {
            account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
        } else {
            account.setCurrentBalance(account.getCurrentBalance().add(amount));
        }
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