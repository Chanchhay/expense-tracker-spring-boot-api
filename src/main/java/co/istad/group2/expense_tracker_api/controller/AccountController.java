package co.istad.group2.expense_tracker_api.controller;

import co.istad.group2.expense_tracker_api.dto.request.createReq.CreateAccountRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateAccountRequest;
import co.istad.group2.expense_tracker_api.dto.response.accountResponse.AccountResponse;
import co.istad.group2.expense_tracker_api.service.AccountService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request,
                                         Authentication authentication) {
        String email = authentication.getName();
        log.info("Create account for user={}", email);
        return accountService.createAccount(request, email);
    }

    @GetMapping
    public List<AccountResponse> getMyAccounts(Authentication authentication) {
        String email = authentication.getName();
        log.info("Get accounts for user={}", email);
        return accountService.getMyAccounts(email);
    }

    @GetMapping("/{id}")
    public AccountResponse getAccountById(@PathVariable String id,
                                          Authentication authentication) {
        String email = authentication.getName();
        log.info("Get account id={} for user={}", id, email);
        return accountService.getAccountById(id, email);
    }

    @PutMapping("/{id}")
    public AccountResponse updateAccount(@PathVariable String id,
                                         @Valid @RequestBody UpdateAccountRequest request,
                                         Authentication authentication) {
        String email = authentication.getName();
        log.info("Update account id={} for user={}", id, email);
        return accountService.updateAccount(id, request, email);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable String id,
                              Authentication authentication) {
        String email = authentication.getName();
        log.info("Delete account id={} for user={}", id, email);
        accountService.deleteAccount(id, email);
    }
}