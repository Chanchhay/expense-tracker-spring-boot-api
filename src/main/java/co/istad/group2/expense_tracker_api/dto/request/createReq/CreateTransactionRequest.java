package co.istad.group2.expense_tracker_api.dto.request.createReq;

import co.istad.group2.expense_tracker_api.domain.TransactionImage;
import co.istad.group2.expense_tracker_api.domain.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateTransactionRequest(

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotNull
        TransactionType type,

        @NotBlank
        String accountId,

        @NotNull
        Integer categoryId,

        @NotNull
        LocalDate date,

        @NotBlank
        String source,

        String note,
        List<TransactionImageRequest> images
) {
}
