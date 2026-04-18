package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.RefreshToken;
import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.domain.enums.Role;
import co.istad.group2.expense_tracker_api.dto.request.createReq.LoginRequest;
import co.istad.group2.expense_tracker_api.dto.request.createReq.RegisterRequest;
import co.istad.group2.expense_tracker_api.dto.response.AuthTokensResponse;
import co.istad.group2.expense_tracker_api.exception.BadRequestException;
import co.istad.group2.expense_tracker_api.exception.ConflictException;
import co.istad.group2.expense_tracker_api.exception.NotFoundException;
import co.istad.group2.expense_tracker_api.repository.UserRepository;
import co.istad.group2.expense_tracker_api.service.AuthService;
import co.istad.group2.expense_tracker_api.service.JwtService;
import co.istad.group2.expense_tracker_api.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager,
                           RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public AuthTokensResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists");
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        User user = new User();
        String name = request.name().trim().replaceAll("\\s+", " ");
        user.setName(name);
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        return generateAndStoreTokens(savedUser);
    }

    @Override
    public AuthTokensResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("User not found"));

        return generateAndStoreTokens(user);
    }

    @Override
    public AuthTokensResponse refresh(HttpServletRequest request) {
        String refreshToken = extractCookieValue(request);
        System.out.println("REFRESH COOKIE TOKEN: " + refreshToken);
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException("Refresh token is missing");
        }

        RefreshToken storedToken = refreshTokenService.verify(refreshToken);
            System.out.println("REFRESH TOKEN FOUND IN DB FOR USER: " + storedToken.getUser().getEmail());
        User user = storedToken.getUser();

        UserDetails userDetails = buildUserDetails(user);

        if (!jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
            throw new BadRequestException("Invalid refresh token");
        }

        refreshTokenService.revokeByToken(refreshToken);

        return generateAndStoreTokens(user);
    }

    @Override
    public void logout(HttpServletRequest request) {
        String refreshToken = extractCookieValue(request);
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.revokeByToken(refreshToken);
        }
    }

    private AuthTokensResponse generateAndStoreTokens(User user) {
        UserDetails userDetails = buildUserDetails(user);

        String accessToken = jwtService.generateAccessToken(
                userDetails,
                user.getRole().name(),
                user.getId()
        );

        String refreshToken = jwtService.generateRefreshToken(
                userDetails,
                user.getId()
        );

        refreshTokenService.save(user, refreshToken, jwtService.getRefreshExpirationSeconds());

        return new AuthTokensResponse(accessToken, refreshToken);
    }

    private UserDetails buildUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword() == null ? "" : user.getPassword(),
                Set.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    private String extractCookieValue(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("refresh_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}