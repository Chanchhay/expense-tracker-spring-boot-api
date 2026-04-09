package co.istad.group2.expense_tracker_api.dto.request.createReq;

import co.istad.group2.expense_tracker_api.domain.enums.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank
        String name,

        @NotNull
        AccountType type,

        @NotBlank
        @Size(min = 3, max = 3, message = "Currency cannot be more than 3 characters")
        String currency,

        @NotNull
        @DecimalMin(value = "0.00")
        BigDecimal initialBalance
) {
}
