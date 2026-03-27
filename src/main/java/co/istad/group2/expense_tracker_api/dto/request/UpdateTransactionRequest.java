package co.istad.group2.expense_tracker_api.dto.request;

import co.istad.group2.expense_tracker_api.domain.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransactionRequest(
        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotNull
        TransactionType type,

        @NotNull
        Integer categoryId,

        @NotBlank
        String currency,

        @NotNull
        LocalDate date,

        String note
) {
}
