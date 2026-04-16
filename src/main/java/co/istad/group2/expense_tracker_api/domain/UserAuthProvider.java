package co.istad.group2.expense_tracker_api.domain;

import co.istad.group2.expense_tracker_api.domain.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_auth_providers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"provider", "providerUserId"})
        }
)
@Getter
@Setter
public class UserAuthProvider {
    @Id
    @GeneratedValue
            (strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    @Column
    private String providerUserId;

    @Column
    private String providerEmail;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
