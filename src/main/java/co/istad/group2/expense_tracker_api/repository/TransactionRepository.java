package co.istad.group2.expense_tracker_api.repository;

import co.istad.group2.expense_tracker_api.domain.Transaction;
import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.domain.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {


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
}