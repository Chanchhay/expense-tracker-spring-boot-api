package co.istad.group2.expense_tracker_api.dto.response.budgetResponse;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record BudgetResponse(
        String id,
        Integer categoryId,
        String categoryName,
        BigDecimal amount,
        Integer month,
        Integer year,
        LocalDateTime createdAt
) {
}
