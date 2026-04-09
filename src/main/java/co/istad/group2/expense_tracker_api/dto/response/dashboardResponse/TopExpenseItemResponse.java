package co.istad.group2.expense_tracker_api.dto.response.dashboardResponse;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record TopExpenseItemResponse(
        String transactionId,
        String accountId,
        String accountName,
        Integer categoryId,
        String categoryName,
        BigDecimal amount,
        String currency,
        LocalDate date,
        String note
        ) {
}
