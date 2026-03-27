package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.dto.request.CreateCategoryRequest;
import co.istad.group2.expense_tracker_api.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
        CategoryResponse createCategory(CreateCategoryRequest request, String email);
    List<CategoryResponse> getMyCategories(String email);
}
