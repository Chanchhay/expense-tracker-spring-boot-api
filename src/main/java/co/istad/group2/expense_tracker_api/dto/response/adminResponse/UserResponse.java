package co.istad.group2.expense_tracker_api.dto.response.adminResponse;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
        String id,
        String name,
        String email,
        String role,
        Boolean isActive,
        LocalDateTime createdAt,
        String profile
) {
}
