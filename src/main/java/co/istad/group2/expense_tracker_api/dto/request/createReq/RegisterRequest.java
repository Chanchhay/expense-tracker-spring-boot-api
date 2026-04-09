package co.istad.group2.expense_tracker_api.dto.request.createReq;

import jakarta.validation.constraints.*;

public record RegisterRequest(

        @NotBlank(message = "Name is required")
        @Size(min = 5, max = 30, message = "Name must be between 5 and 30 characters")
        @Pattern(
                regexp = "^(?!\\s)(.*\\S)?$",
                message = "Name must not have leading or trailing spaces"
        )
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email is not valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
        @Pattern(
                regexp = "^(?!\\s)(.*\\S)?$",
                message = "Password must not have leading or trailing spaces"
        )
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {
}