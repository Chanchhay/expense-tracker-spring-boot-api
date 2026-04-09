package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.dto.request.createReq.CreateBudgetRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateBudgetRequest;
import co.istad.group2.expense_tracker_api.dto.response.budgetResponse.BudgetResponse;
import co.istad.group2.expense_tracker_api.dto.response.budgetResponse.BudgetSummaryResponse;

import java.util.List;

public interface BudgetService {
    BudgetResponse createBudget(CreateBudgetRequest request, String email);
    List<BudgetResponse> getMyBudgets(String email);
    BudgetResponse getBudgetById(String id, String email);
    BudgetResponse updateBudget(String id, UpdateBudgetRequest request, String email);
    void deleteBudget(String id, String email);
    BudgetSummaryResponse getBudgetSummary(String email, String month);
}