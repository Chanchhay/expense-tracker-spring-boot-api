package co.istad.group2.expense_tracker_api.repository;

import co.istad.group2.expense_tracker_api.domain.SavingsGoal;
import co.istad.group2.expense_tracker_api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, String> {

    List<SavingsGoal> findAllByUserOrderByCreatedAtDesc(User user);

    Optional<SavingsGoal> findByIdAndUser(String id, User user);
}