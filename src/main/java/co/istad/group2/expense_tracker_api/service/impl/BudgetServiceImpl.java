package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.Budget;
import co.istad.group2.expense_tracker_api.domain.Category;
import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.dto.request.createReq.CreateBudgetRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateBudgetRequest;
import co.istad.group2.expense_tracker_api.dto.response.budgetResponse.BudgetResponse;
import co.istad.group2.expense_tracker_api.dto.response.budgetResponse.BudgetSummaryItemResponse;
import co.istad.group2.expense_tracker_api.dto.response.budgetResponse.BudgetSummaryResponse;
import co.istad.group2.expense_tracker_api.exception.ConflictException;
import co.istad.group2.expense_tracker_api.exception.NotFoundException;
import co.istad.group2.expense_tracker_api.repository.BudgetRepository;
import co.istad.group2.expense_tracker_api.repository.CategoryRepository;
import co.istad.group2.expense_tracker_api.repository.TransactionRepository;
import co.istad.group2.expense_tracker_api.repository.UserRepository;
import co.istad.group2.expense_tracker_api.service.BudgetService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public BudgetServiceImpl(BudgetRepository budgetRepository,
                             UserRepository userRepository,
                             CategoryRepository categoryRepository,
                             TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public BudgetResponse createBudget(CreateBudgetRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        Category category = categoryRepository.findByIdAndUserOrUserIdIsNullAndId(request.categoryId(), user, request.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found or does not belong to user"));

        boolean exists = budgetRepository.existsByUserAndCategoryAndMonthAndYear(
                user,
                category,
                request.month(),
                request.year()
        );

        if (exists) {
            throw new ConflictException("Budget already exists for this category and month");
        }

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(category);
        budget.setAmount(request.amount());
        budget.setMonth(request.month());
        budget.setYear(request.year());

        Budget savedBudget = budgetRepository.save(budget);
        return mapToResponse(savedBudget);
    }

    @Override
    public List<BudgetResponse> getMyBudgets(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        return budgetRepository.findAllByUserOrderByYearDescMonthDescCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BudgetResponse getBudgetById(String id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        Budget budget = budgetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Budget not found or does not belong to user"));

        return mapToResponse(budget);
    }

    @Override
    public BudgetResponse updateBudget(String id, UpdateBudgetRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        Budget budget = budgetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Budget not found or does not belong to user"));

        Category category = categoryRepository.findByIdAndUserOrUserIdIsNullAndId(request.categoryId(), user, request.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found or does not belong to user"));

        boolean changedIdentity =
                !budget.getCategory().getId().equals(category.getId()) ||
                !budget.getMonth().equals(request.month()) ||
                !budget.getYear().equals(request.year());

        if (changedIdentity) {
            boolean exists = budgetRepository.existsByUserAndCategoryAndMonthAndYear(
                    user,
                    category,
                    request.month(),
                    request.year()
            );

            if (exists) {
                throw new ConflictException("Budget already exists for this category and month");
            }
        }

        budget.setCategory(category);
        budget.setAmount(request.amount());
        budget.setMonth(request.month());
        budget.setYear(request.year());

        Budget updatedBudget = budgetRepository.save(budget);
        return mapToResponse(updatedBudget);
    }

    @Override
    public void deleteBudget(String id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        Budget budget = budgetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Budget not found or does not belong to user"));

        budgetRepository.delete(budget);
    }

    @Override
    public BudgetSummaryResponse getBudgetSummary(String email, String month) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        YearMonth yearMonth = YearMonth.parse(month); // format: 2026-04
        int targetMonth = yearMonth.getMonthValue();
        int targetYear = yearMonth.getYear();

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.plusMonths(1).atDay(1);

        List<Budget> budgets = budgetRepository.findAllByUserAndMonthAndYearOrderByCreatedAtDesc(
                user,
                targetMonth,
                targetYear
        );

        List<BudgetSummaryItemResponse> items = budgets.stream()
                .map(budget -> {
                    BigDecimal spentAmount = transactionRepository.sumExpenseByUserAndCategoryAndDateRange(
                            user,
                            budget.getCategory(),
                            startDate,
                            endDate
                    );

                    BigDecimal remainingAmount = budget.getAmount().subtract(spentAmount);

                    BigDecimal percentageUsed = BigDecimal.ZERO;
                    if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                        percentageUsed = spentAmount
                                .multiply(BigDecimal.valueOf(100))
                                .divide(budget.getAmount(), 2, RoundingMode.HALF_UP);
                    }

                    return BudgetSummaryItemResponse.builder()
                            .budgetId(budget.getId())
                            .categoryId(budget.getCategory().getId())
                            .categoryName(budget.getCategory().getName())
                            .budgetAmount(budget.getAmount())
                            .spentAmount(spentAmount)
                            .remainingAmount(remainingAmount)
                            .percentageUsed(percentageUsed)
                            .build();
                })
                .toList();

        BigDecimal totalBudget = items.stream()
                .map(BudgetSummaryItemResponse::budgetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSpent = items.stream()
                .map(BudgetSummaryItemResponse::spentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRemaining = totalBudget.subtract(totalSpent);

        return BudgetSummaryResponse.builder()
                .month(month)
                .totalBudget(totalBudget)
                .totalSpent(totalSpent)
                .totalRemaining(totalRemaining)
                .items(items)
                .build();
    }

    private BudgetResponse mapToResponse(Budget budget) {
        return BudgetResponse.builder()
                .id(budget.getId())
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .amount(budget.getAmount())
                .month(budget.getMonth())
                .year(budget.getYear())
                .createdAt(budget.getCreatedAt())
                .build();
    }
}