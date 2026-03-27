package co.istad.group2.expense_tracker_api.dto.request;

import co.istad.group2.expense_tracker_api.domain.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
        @NotNull
        Role role
) {
}
