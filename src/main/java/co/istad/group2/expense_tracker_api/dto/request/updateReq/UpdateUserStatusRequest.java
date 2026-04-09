package co.istad.group2.expense_tracker_api.dto.request.updateReq;

import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull
        Boolean isActive
) {
}
