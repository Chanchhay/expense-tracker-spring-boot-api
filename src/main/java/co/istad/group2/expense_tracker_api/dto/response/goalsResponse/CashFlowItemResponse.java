package co.istad.group2.expense_tracker_api.dto.response.goalsResponse;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CashFlowItemResponse(
        String period,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net
) {
}
