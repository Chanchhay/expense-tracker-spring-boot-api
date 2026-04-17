package co.istad.group2.expense_tracker_api.dto.response.accountResponse;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CurrencyBalanceTotalResponse(
        String currency,
        BigDecimal totalBalance
) {
}
