package co.istad.group2.expense_tracker_api.dto.response.categoryResponse;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CategoryBreakdownItemResponse(
        Integer categoryId,
        String categoryName,
        BigDecimal amount,
        BigDecimal percentage
) {
}
