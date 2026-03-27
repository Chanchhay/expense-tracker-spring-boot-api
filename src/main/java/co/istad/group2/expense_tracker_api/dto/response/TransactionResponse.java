package co.istad.group2.expense_tracker_api.dto.response;

import co.istad.group2.expense_tracker_api.domain.enums.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record TransactionResponse(
        String id,
        BigDecimal amount,
        TransactionType type,
        Integer categoryId,
        String categoryName,
        String currency,
        LocalDate date,
        String note
) {
}
