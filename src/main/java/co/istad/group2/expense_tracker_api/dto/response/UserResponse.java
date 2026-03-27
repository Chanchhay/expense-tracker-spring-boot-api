package co.istad.group2.expense_tracker_api.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(String uuid, String name, String email, LocalDateTime createdDate) {
}
