package co.istad.group2.expense_tracker_api.dto.response.accountResponse;

import co.istad.group2.expense_tracker_api.domain.enums.AccountType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AccountResponse(
        String id,
        String name,
        AccountType type,
        String currency,
        BigDecimal initialBalance,
        BigDecimal currentBalance,
        LocalDateTime createdAt
) {
}
