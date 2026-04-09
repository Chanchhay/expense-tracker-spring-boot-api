package co.istad.group2.expense_tracker_api.controller;

import co.istad.group2.expense_tracker_api.dto.request.createReq.CreateCategoryRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateCategoryRequest;
import co.istad.group2.expense_tracker_api.dto.response.categoryResponse.CategoryResponse;
import co.istad.group2.expense_tracker_api.service.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryResponse createCategory(@Valid @RequestBody CreateCategoryRequest request,
                                           Authentication authentication) {
        String email = authentication.getName();
        log.info("Create category for user={}", email);
        return categoryService.createCategory(request, email);
    }

    @GetMapping
    public List<CategoryResponse> getMyCategories(Authentication authentication) {
        String email = authentication.getName();
        log.info("Get categories for user={}", email);
        return categoryService.getMyCategories(email);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Integer id, Authentication authentication) {
        String email = authentication.getName();
        categoryService.deleteCategory(id, email);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateCategoryRequest request, Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(categoryService.updateCategory(id, request, email));
    }
}