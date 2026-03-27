package co.istad.group2.expense_tracker_api.dto.response;

import lombok.Builder;

@Builder
public record CategoryResponse(Integer id, String name) {
}
