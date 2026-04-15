package co.istad.group2.expense_tracker_api.dto.request.updateReq;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateSavingsGoalRequest(
        @NotBlank
        String name,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal targetAmount,

        LocalDate deadline,
        String image
) {
}
