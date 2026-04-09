package co.istad.group2.expense_tracker_api.dto.request.createReq;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateBudgetRequest(
    @NotNull
    Integer categoryId,

    @NotNull
    @DecimalMin(value = "0.01")
    BigDecimal amount,

    @NotNull
    @Min(1)
    @Max(12)
    Integer month,

    @NotNull
    @Min(2026)
    @Max(3000)
    Integer year
) {
}
