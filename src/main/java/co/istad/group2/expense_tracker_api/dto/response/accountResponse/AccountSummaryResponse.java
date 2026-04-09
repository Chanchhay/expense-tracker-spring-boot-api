package co.istad.group2.expense_tracker_api.dto.response.accountResponse;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record AccountSummaryResponse(
        BigDecimal totalCurrentBalance,
        List<AccountSummaryItemResponse> items
) {
}
