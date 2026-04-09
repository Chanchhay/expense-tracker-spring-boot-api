package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.dto.request.createReq.CreateSavingsGoalRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateSavingsGoalProgressRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateSavingsGoalRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateSavingsGoalStatusRequest;
import co.istad.group2.expense_tracker_api.dto.response.goalsResponse.SavingsGoalResponse;

import java.util.List;

public interface SavingsGoalService {
    SavingsGoalResponse createSavingsGoal(CreateSavingsGoalRequest request, String email);
    List<SavingsGoalResponse> getMySavingsGoals(String email);
    SavingsGoalResponse getSavingsGoalById(String id, String email);
    SavingsGoalResponse updateSavingsGoal(String id, UpdateSavingsGoalRequest request, String email);
    SavingsGoalResponse updateSavingsGoalProgress(String id, UpdateSavingsGoalProgressRequest request, String email);
    SavingsGoalResponse updateSavingsGoalStatus(String id, UpdateSavingsGoalStatusRequest request, String email);
    void deleteSavingsGoal(String id, String email);
}