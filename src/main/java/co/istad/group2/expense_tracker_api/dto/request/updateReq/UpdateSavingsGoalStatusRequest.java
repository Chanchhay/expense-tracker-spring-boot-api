package co.istad.group2.expense_tracker_api.dto.request.updateReq;

import co.istad.group2.expense_tracker_api.domain.enums.GoalStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateSavingsGoalStatusRequest(
        @NotNull
        GoalStatus status
) {
}
