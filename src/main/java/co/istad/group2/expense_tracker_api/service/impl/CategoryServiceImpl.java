package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.Category;
import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.domain.enums.CategoryType;
import co.istad.group2.expense_tracker_api.dto.request.createReq.CreateCategoryRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateCategoryRequest;
import co.istad.group2.expense_tracker_api.dto.response.categoryResponse.CategoryResponse;
import co.istad.group2.expense_tracker_api.exception.ConflictException;
import co.istad.group2.expense_tracker_api.exception.NotFoundException;
import co.istad.group2.expense_tracker_api.repository.BudgetRepository;
import co.istad.group2.expense_tracker_api.repository.CategoryRepository;
import co.istad.group2.expense_tracker_api.repository.TransactionRepository;
import co.istad.group2.expense_tracker_api.repository.UserRepository;
import co.istad.group2.expense_tracker_api.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               UserRepository userRepository, TransactionRepository transactionRepository, BudgetRepository budgetRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        String name = request.name().trim().replaceAll("\\s+", " ");
        String type = request.type().trim().replaceAll("\\s+", " ").toUpperCase();
        boolean exists = categoryRepository.existsByNameIgnoreCaseAndUserAndType((name), user, CategoryType.valueOf(type));
        if (exists) {
            throw new ConflictException("Category already exists for this user");
        }

        Category category = new Category();
        category.setName(name);
        category.setType(CategoryType.valueOf(type));
        category.setUser(user);

        Category savedCategory = categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    @Override
    public List<CategoryResponse> getMyCategories(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

//        return categoryRepository.findAllByUserOrderByCreatedAtDesc(user)
//                .stream()
//                .map(this::mapToResponse)
//                .toList();
        return categoryRepository.findAvailableCategoriesForUser(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

//    @Override
//    public void deleteCategory(Integer id, String email) {
//        User currentUser = userRepository.findByEmail(email)
//                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
//
//        Category category = categoryRepository.findByIdAndUserId(id, currentUser.getId())
//                .orElseThrow(() -> new NotFoundException("Category not found"));
//
//        boolean usedInTransactions = transactionRepository.existsByCategoryIdAndUserId(id, currentUser.getId());
//        if (usedInTransactions) {
//            throw new BadRequestException("Cannot delete category because it is used by transactions");
//        }
//
//        boolean usedInBudgets = budgetRepository.existsByCategoryIdAndUserId(id, currentUser.getId());
//        if (usedInBudgets) {
//            throw new BadRequestException("Cannot delete category because it is used by budgets");
//        }
//
//        boolean usedInRecurringTransactions =
//                transactionRepository.existsByCategoryIdAndUserId(id, currentUser.getId());
//        if (usedInRecurringTransactions) {
//            throw new BadRequestException("Cannot delete category because it is used by recurring transactions");
//        }
//
//        categoryRepository.delete(category);
//    }

    @Override
    public CategoryResponse updateCategory(Integer id, UpdateCategoryRequest request, String email) {
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        Category category = categoryRepository.findByIdAndUserIdAndIsDefaultFalse(id, currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        boolean exists = categoryRepository.existsVisibleCategoryByNameAndTypeAndIdNot(
                currentUser.getId(),
                request.name(),
                request.type(),
                id
        );

        if (exists) {
            throw new IllegalArgumentException("Category already exists");
        }

        category.setName(request.name().trim());
        category.setType(request.type());

        categoryRepository.save(category);

        return mapToResponse(category);
    }

    @Override
    public void deleteCategory(Integer id, String email) {
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        Category category = categoryRepository.findByIdAndUserIdAndIsDefaultFalse(id, currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        boolean usedInTransactions = transactionRepository.existsByCategoryIdAndUserId(id, currentUser.getId());
        boolean usedInBudgets = budgetRepository.existsByCategoryIdAndUserId(id, currentUser.getId());
        boolean usedInRecurring = transactionRepository.existsByCategoryIdAndUserId(id, currentUser.getId());

        if (usedInTransactions || usedInBudgets || usedInRecurring) {
            throw new IllegalStateException("Cannot delete category because it is already in use");
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .isDefault(category.getIsDefault())
                .createdAt(category.getCreatedAt())
                .build();
    }
}