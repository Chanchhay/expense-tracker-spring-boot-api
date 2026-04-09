package co.istad.group2.expense_tracker_api.dto.request.updateReq;

import co.istad.group2.expense_tracker_api.domain.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(

        @NotBlank
        @Size(min=3, max = 20, message = "Category cannot be more than 20 characters")
        String name,

        @NotNull
        CategoryType type
) {
}