package co.istad.group2.expense_tracker_api.dto.response.goalsResponse;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record CashFlowResponse(
        LocalDate from,
        LocalDate to,
        String groupBy,
        List<CashFlowItemResponse> items
) {
}
