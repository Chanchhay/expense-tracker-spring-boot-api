package co.istad.group2.expense_tracker_api.dto.response.accountResponse;

import lombok.Builder;

import java.util.List;

@Builder
public record AccountSummaryResponse(
        List<CurrencyBalanceTotalResponse> totalsByCurrency,
        List<AccountSummaryItemResponse> items
) {
}