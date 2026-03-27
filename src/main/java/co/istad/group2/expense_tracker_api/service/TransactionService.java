package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.domain.enums.TransactionType;
import co.istad.group2.expense_tracker_api.dto.request.CreateTransactionRequest;
import co.istad.group2.expense_tracker_api.dto.request.UpdateTransactionRequest;
import co.istad.group2.expense_tracker_api.dto.response.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(CreateTransactionRequest request, String email);
    List<TransactionResponse> getMyTransactions(String email, TransactionType type, String month);
    TransactionResponse updateTransaction(String id, UpdateTransactionRequest request, String email);
    void deleteTransaction(String id, String email);
}