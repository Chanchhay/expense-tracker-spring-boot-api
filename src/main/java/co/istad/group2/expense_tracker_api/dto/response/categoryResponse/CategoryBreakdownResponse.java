package co.istad.group2.expense_tracker_api.dto.response.categoryResponse;

import lombok.Builder;

import java.util.List;

@Builder
public record CategoryBreakdownResponse(
        String month,
        List<CategoryBreakdownCurrencyGroupResponse> groups
) {
}
