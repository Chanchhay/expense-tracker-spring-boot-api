package co.istad.group2.expense_tracker_api.dto.request.updateReq;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateSavingsGoalProgressRequest(
        @NotNull
        @DecimalMin(value = "0.00")
        BigDecimal currentAmount
) {
}
