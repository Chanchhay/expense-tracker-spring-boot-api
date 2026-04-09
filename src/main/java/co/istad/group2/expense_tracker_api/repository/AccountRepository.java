package co.istad.group2.expense_tracker_api.repository;

import co.istad.group2.expense_tracker_api.domain.Account;
import co.istad.group2.expense_tracker_api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByNameIgnoreCaseAndUserAndCurrency(String name, User user, String currency);
    List<Account> findAllByUserOrderByCreatedAtDesc(User user);
    Optional<Account> findByIdAndUser(String id, User user);
}