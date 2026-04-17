package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.domain.RefreshToken;
import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.exception.BadRequestException;
import co.istad.group2.expense_tracker_api.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void revokeAllUserTokens(User user) {
        List<RefreshToken> tokens = refreshTokenRepository.findByUserAndRevokedFalse(user);
        for (RefreshToken token : tokens) {
            token.setRevoked(true);
        }
        refreshTokenRepository.saveAll(tokens);
    }

    public void save(User user, String token, long refreshExpirationSeconds) {
        revokeAllUserTokens(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshExpirationSeconds));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verify(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Refresh token not found"));

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw new BadRequestException("Refresh token revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token expired");
        }

        return refreshToken;
    }

    public void revokeByToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }
}
