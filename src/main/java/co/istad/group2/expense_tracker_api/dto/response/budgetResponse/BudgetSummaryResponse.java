package co.istad.group2.expense_tracker_api.dto.response.budgetResponse;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record BudgetSummaryResponse(
        String month,
        BigDecimal totalBudget,
        BigDecimal totalSpent,
        BigDecimal totalRemaining,
        List<BudgetSummaryItemResponse> items
) {
}
