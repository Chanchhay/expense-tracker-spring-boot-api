package co.istad.group2.expense_tracker_api.dto.response.budgetResponse;

import lombok.Builder;

import java.util.List;

@Builder
public record BudgetSummaryResponse(
        String month,
        List<BudgetCurrencyTotalResponse> totalsByCurrency,
        List<BudgetSummaryItemResponse> items
) {
}