package co.istad.group2.expense_tracker_api.dto.request.updateReq;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateBudgetRequest(
        @NotNull
        Integer categoryId,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotBlank
        @Size(min = 3, max = 3)
        String currency,

        @NotNull
        @Min(1)
        @Max(12)
        Integer month,

        @NotNull
        @Min(2000)
        @Max(3000)
        Integer year
) {
}