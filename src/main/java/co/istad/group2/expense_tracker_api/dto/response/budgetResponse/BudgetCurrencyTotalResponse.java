package co.istad.group2.expense_tracker_api.dto.response.budgetResponse;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BudgetCurrencyTotalResponse(
        String currency,
        BigDecimal totalBudget,
        BigDecimal totalSpent,
        BigDecimal totalRemaining
) {
}
