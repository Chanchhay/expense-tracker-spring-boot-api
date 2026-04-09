package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.dto.request.createReq.CreateAccountRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateAccountRequest;
import co.istad.group2.expense_tracker_api.dto.response.accountResponse.AccountResponse;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(CreateAccountRequest request, String email);
    List<AccountResponse> getMyAccounts(String email);
    AccountResponse getAccountById(String id, String email);
    AccountResponse updateAccount(String id, UpdateAccountRequest request, String email);
    void deleteAccount(String id, String email);
}