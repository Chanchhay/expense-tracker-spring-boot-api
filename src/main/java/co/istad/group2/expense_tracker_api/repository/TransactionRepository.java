package co.istad.group2.expense_tracker_api.repository;

import co.istad.group2.expense_tracker_api.domain.Account;
import co.istad.group2.expense_tracker_api.domain.Transaction;
import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.domain.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    boolean existsByCategoryIdAndUserId(Integer categoryId, String userId);

    List<Transaction> findByUserOrderByDateDescCreatedAtDesc(User user);

    List<Transaction> findByUserAndTypeOrderByDateDescCreatedAtDesc(User user, TransactionType type);

    List<Transaction> findByUserAndDateGreaterThanEqualAndDateLessThanOrderByDateDescCreatedAtDesc(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Transaction> findByUserAndTypeAndDateGreaterThanEqualAndDateLessThanOrderByDateDescCreatedAtDesc(
            User user,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user = :user
              AND t.type = :type
            """)
    BigDecimal sumAmountByUserAndType(User user, TransactionType type);

    List<Transaction> findTop5ByUserOrderByDateDescCreatedAtDesc(User user);

    Optional<Transaction> findByIdAndUser(String id, User user);

    boolean existsByAccount(Account account);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user = :user
              AND t.category = :category
              AND t.type = co.istad.group2.expense_tracker_api.domain.enums.TransactionType.EXPENSE
              AND t.date >= :startDate
              AND t.date < :endDate
            """)
    java.math.BigDecimal sumExpenseByUserAndCategoryAndDateRange(
            co.istad.group2.expense_tracker_api.domain.User user,
            co.istad.group2.expense_tracker_api.domain.Category category,
            java.time.LocalDate startDate,
            java.time.LocalDate endDate
    );

    List<Transaction> findByUserAndDateGreaterThanEqualAndDateLessThanOrderByDateAscCreatedAtAsc(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.user = :user
          AND t.category = :category
          AND t.currency = :currency
          AND t.type = co.istad.group2.expense_tracker_api.domain.enums.TransactionType.EXPENSE
          AND t.date >= :startDate
          AND t.date < :endDate
        """)
BigDecimal sumExpenseByUserAndCategoryAndCurrencyAndDateRange(
        User user,
        co.istad.group2.expense_tracker_api.domain.Category category,
        String currency,
        LocalDate startDate,
        LocalDate endDate
);
}