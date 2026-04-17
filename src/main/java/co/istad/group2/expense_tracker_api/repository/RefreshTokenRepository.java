package co.istad.group2.expense_tracker_api.repository;

import co.istad.group2.expense_tracker_api.domain.RefreshToken;
import co.istad.group2.expense_tracker_api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByUserAndRevokedFalse(User user);
}
