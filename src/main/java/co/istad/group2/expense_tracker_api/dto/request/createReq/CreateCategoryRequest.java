package co.istad.group2.expense_tracker_api.dto.request.createReq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank
        @Size(min = 3, max = 20, message = "Category cannot be more than 20 characters")
        @Pattern(regexp = "^(?=\\S).*\\S$", message = "No leading or trailing whitespace allowed")
        String name,

        @NotNull
        String type
) {
}
