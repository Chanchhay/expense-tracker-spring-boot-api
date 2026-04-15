package co.istad.group2.expense_tracker_api.dto.response.adminResponse;

import lombok.Builder;

@Builder
public record UpdateUserProfileResponse(
        String name,
        String profile
) {
}
