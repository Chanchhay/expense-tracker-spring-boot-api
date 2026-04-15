package co.istad.group2.expense_tracker_api.dto.response.transactionResponse;

import co.istad.group2.expense_tracker_api.domain.enums.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record TransactionResponse(
        String id,
        BigDecimal amount,
        TransactionType type,
        String accountId,
        String accountName,
        Integer categoryId,
        String categoryName,
        String currency,
        LocalDate date,
        String source,
        String note,
        List<TransactionImageResponse> images
) {
}
