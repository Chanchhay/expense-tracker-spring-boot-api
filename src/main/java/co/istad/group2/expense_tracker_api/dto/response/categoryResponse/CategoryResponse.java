package co.istad.group2.expense_tracker_api.dto.response.categoryResponse;

import co.istad.group2.expense_tracker_api.domain.enums.CategoryType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CategoryResponse(Integer id,
        String name,
        CategoryType type,
        Boolean isDefault,
        LocalDateTime createdAt) {
}
