package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.Account;
import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.dto.request.createReq.CreateAccountRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateAccountRequest;
import co.istad.group2.expense_tracker_api.dto.response.accountResponse.AccountResponse;
import co.istad.group2.expense_tracker_api.exception.ConflictException;
import co.istad.group2.expense_tracker_api.exception.NotFoundException;
import co.istad.group2.expense_tracker_api.repository.AccountRepository;
import co.istad.group2.expense_tracker_api.repository.TransactionRepository;
import co.istad.group2.expense_tracker_api.repository.UserRepository;
import co.istad.group2.expense_tracker_api.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public AccountServiceImpl(AccountRepository accountRepository,
                              UserRepository userRepository,
                              TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public AccountResponse createAccount(CreateAccountRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        if (accountRepository.existsByNameIgnoreCaseAndUserAndCurrency(request.name().trim(), user, request.currency())) {
            throw new ConflictException("Account already exists for this user");
        }

        Account account = new Account();
        account.setName(request.name().trim().replaceAll("\\s+", " "));
        account.setType(request.type());
        account.setCurrency(request.currency().trim().toUpperCase());
        account.setInitialBalance(request.initialBalance());
        account.setCurrentBalance(request.initialBalance());
        account.setUser(user);

        Account savedAccount = accountRepository.save(account);
        return mapToResponse(savedAccount);
    }

    @Override
    public List<AccountResponse> getMyAccounts(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        return accountRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AccountResponse getAccountById(String id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        Account account = accountRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Account not found or does not belong to user"));

        return mapToResponse(account);
    }

    @Override
    public AccountResponse updateAccount(String id, UpdateAccountRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        Account account = accountRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Account not found or does not belong to user"));

        if (!account.getName().equalsIgnoreCase(request.name().trim())
                && accountRepository.existsByNameIgnoreCaseAndUserAndCurrency(request.name().trim(), user, request.currency())) {
            throw new ConflictException("Account already exists for this user");
        }

        account.setName(request.name().trim().replaceAll("\\s+", " "));
        account.setType(request.type());
        account.setCurrency(request.currency().trim().replaceAll("\\s+", " ").toUpperCase());
        account.setCurrentBalance(request.initialBalance());

        Account updatedAccount = accountRepository.save(account);
        return mapToResponse(updatedAccount);
    }

    @Override
    public void deleteAccount(String id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        Account account = accountRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Account not found or does not belong to user"));

        if (transactionRepository.existsByAccount(account)) {
            throw new ConflictException("Cannot delete account because it already has transactions");
        }

        accountRepository.delete(account);
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .type(account.getType())
                .currency(account.getCurrency())
                .initialBalance(account.getInitialBalance())
                .currentBalance(account.getCurrentBalance())
                .createdAt(account.getCreatedAt())
                .build();
    }
}