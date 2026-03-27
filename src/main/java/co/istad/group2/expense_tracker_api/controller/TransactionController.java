package co.istad.group2.expense_tracker_api.controller;

import co.istad.group2.expense_tracker_api.domain.enums.TransactionType;
import co.istad.group2.expense_tracker_api.dto.request.CreateTransactionRequest;
import co.istad.group2.expense_tracker_api.dto.request.UpdateTransactionRequest;
import co.istad.group2.expense_tracker_api.dto.response.TransactionResponse;
import co.istad.group2.expense_tracker_api.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public TransactionResponse createTransaction(@Valid @RequestBody CreateTransactionRequest request,
                                                 Authentication authentication) {
        String email = authentication.getName();
        log.info("Create transaction for user={}", email);
        return transactionService.createTransaction(request, email);
    }

    @GetMapping
    public List<TransactionResponse> getMyTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String month,
            Authentication authentication
    ) {
        String email = authentication.getName();
        log.info("Get transactions for user={}, type={}, month={}", email, type, month);
        return transactionService.getMyTransactions(email, type, month);
    }

    @PutMapping("/{id}")
    public TransactionResponse updateTransaction(@PathVariable String id,
                                                 @Valid @RequestBody UpdateTransactionRequest request,
                                                 Authentication authentication) {
        String email = authentication.getName();
        log.info("Update transaction id={} for user={}", id, email);
        return transactionService.updateTransaction(id, request, email);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable String id,
                                  Authentication authentication) {
        String email = authentication.getName();
        log.info("Delete transaction id={} for user={}", id, email);
        transactionService.deleteTransaction(id, email);
    }
}