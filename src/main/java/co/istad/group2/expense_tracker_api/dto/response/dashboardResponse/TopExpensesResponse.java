package co.istad.group2.expense_tracker_api.dto.response.dashboardResponse;

import lombok.Builder;

import java.util.List;

@Builder
public record TopExpensesResponse(
        String month,
        Integer limit,
        List<TopExpenseItemResponse> items
) {
}
