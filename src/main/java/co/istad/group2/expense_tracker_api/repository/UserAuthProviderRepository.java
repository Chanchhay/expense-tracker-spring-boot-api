package co.istad.group2.expense_tracker_api.repository;

import co.istad.group2.expense_tracker_api.domain.UserAuthProvider;
import co.istad.group2.expense_tracker_api.domain.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthProviderRepository extends JpaRepository<UserAuthProvider, String> {

    Optional<UserAuthProvider> findByProviderAndProviderUserId(
            AuthProvider provider,
            String providerUserId
    );
}