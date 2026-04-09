package co.istad.group2.expense_tracker_api.dto.response.dashboardResponse;

import co.istad.group2.expense_tracker_api.dto.response.transactionResponse.TransactionResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record DashboardResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal currentBalance,
        List<TransactionResponse> recentTransactions
) {
}
