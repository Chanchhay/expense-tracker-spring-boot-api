package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.dto.request.createReq.CreateCategoryRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateCategoryRequest;
import co.istad.group2.expense_tracker_api.dto.response.categoryResponse.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CreateCategoryRequest request, String email);
    List<CategoryResponse> getMyCategories(String email);

    void deleteCategory(Integer id, String email);

    CategoryResponse updateCategory(Integer id, UpdateCategoryRequest request, String email);
}
