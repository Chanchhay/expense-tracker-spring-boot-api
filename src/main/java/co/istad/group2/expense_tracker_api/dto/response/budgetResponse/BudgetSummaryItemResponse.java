package co.istad.group2.expense_tracker_api.dto.response.budgetResponse;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BudgetSummaryItemResponse(
        String budgetId,
        Integer categoryId,
        String categoryName,
        BigDecimal budgetAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        BigDecimal percentageUsed
) {
}
