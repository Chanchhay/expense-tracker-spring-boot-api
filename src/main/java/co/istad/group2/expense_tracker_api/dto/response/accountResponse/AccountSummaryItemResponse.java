package co.istad.group2.expense_tracker_api.dto.response.accountResponse;

import co.istad.group2.expense_tracker_api.domain.enums.AccountType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountSummaryItemResponse(
        String accountId,
        String accountName,
        AccountType accountType,
        String currency,
        BigDecimal initialBalance,
        BigDecimal currentBalance
) {
}
