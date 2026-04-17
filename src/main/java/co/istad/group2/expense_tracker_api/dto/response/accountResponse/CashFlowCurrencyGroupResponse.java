package co.istad.group2.expense_tracker_api.dto.response.accountResponse;


import co.istad.group2.expense_tracker_api.dto.response.goalsResponse.CashFlowItemResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record CashFlowCurrencyGroupResponse(
        String currency,
        List<CashFlowItemResponse> items
) {
}
