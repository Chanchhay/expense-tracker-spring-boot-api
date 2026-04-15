package co.istad.group2.expense_tracker_api.dto.request.updateReq;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        String profile
) {
}