package co.istad.group2.expense_tracker_api.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull
        Boolean isActive
) {
}
