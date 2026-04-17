package co.istad.group2.expense_tracker_api.repository;

import co.istad.group2.expense_tracker_api.domain.Budget;
import co.istad.group2.expense_tracker_api.domain.Category;
import co.istad.group2.expense_tracker_api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, String> {

    boolean existsByUserAndCategoryAndMonthAndYearAndCurrency(
        User user,
        Category category,
        Integer month,
        Integer year,
        String currency
);

    List<Budget> findAllByUserOrderByYearDescMonthDescCreatedAtDesc(User user);

    List<Budget> findAllByUserAndMonthAndYearOrderByCreatedAtDesc(User user, Integer month, Integer year);

    Optional<Budget> findByIdAndUser(String id, User user);

    boolean existsByCategoryIdAndUserId(Integer categoryId, String userId);
}