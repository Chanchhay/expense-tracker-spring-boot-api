package co.istad.group2.expense_tracker_api.dto.response.dashboardResponse;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MonthlySummaryResponse(
        String month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance
) {
}
