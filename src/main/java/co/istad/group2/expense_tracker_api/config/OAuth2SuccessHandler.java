package co.istad.group2.expense_tracker_api.config;

import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.domain.UserAuthProvider;
import co.istad.group2.expense_tracker_api.domain.enums.AuthProvider;
import co.istad.group2.expense_tracker_api.domain.enums.Role;
import co.istad.group2.expense_tracker_api.repository.UserAuthProviderRepository;
import co.istad.group2.expense_tracker_api.repository.UserRepository;
import co.istad.group2.expense_tracker_api.service.AuthCookieService;
import co.istad.group2.expense_tracker_api.service.JwtService;
import co.istad.group2.expense_tracker_api.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthCookieService authCookieService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull Authentication authentication)
            throws IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        assert token.getPrincipal() != null;

        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        String registrationId = token.getAuthorizedClientRegistrationId();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerUserId;
        String picture = null;
        AuthProvider provider;

        if ("google".equals(registrationId)) {
            provider = AuthProvider.GOOGLE;
            providerUserId = (String) attributes.get("sub");
            picture = (String) attributes.get("picture");
        } else if ("facebook".equals(registrationId)) {
            provider = AuthProvider.FACEBOOK;
            providerUserId = (String) attributes.get("id");

            Object pictureObj = attributes.get("picture");
            if (pictureObj instanceof Map<?, ?> pictureMap) {
                Object dataObj = pictureMap.get("data");
                if (dataObj instanceof Map<?, ?> dataMap) {
                    Object urlObj = dataMap.get("url");
                    if (urlObj instanceof String url) {
                        picture = url;
                    }
                }
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported provider");
            return;
        }

        if (email == null || email.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Provider email is unavailable");
            return;
        }

        email = email.trim().toLowerCase();
        if (name == null || name.isBlank()) {
            name = email;
        } else {
            name = name.trim().replaceAll("\\s+", " ");
        }

        String finalEmail = email;
        String finalName = name;
        User user = userAuthProviderRepository
                .findByProviderAndProviderUserId(provider, providerUserId)
                .map(UserAuthProvider::getUser)
                .orElseGet(() -> {
                    User existingUser = userRepository.findByEmail(finalEmail).orElse(null);

                    if (existingUser != null) {
                        linkProvider(existingUser, provider, providerUserId, finalEmail);
                        return existingUser;
                    }

                    User newUser = new User();
                    newUser.setEmail(finalEmail);
                    newUser.setName(finalName);
                    newUser.setRole(Role.USER);
                    newUser.setIsActive(true);

                    User savedUser = userRepository.save(newUser);
                    linkProvider(savedUser, provider, providerUserId, finalEmail);
                    return savedUser;
                });

        if (user.getProfile() == null && picture != null) {
            user.setProfile(picture);
            userRepository.save(user);
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User is inactive");
            return;
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword() == null ? "" : user.getPassword(),
                Set.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        String accessToken = jwtService.generateAccessToken(userDetails, user.getRole().name(), user.getId());
        String refreshToken = jwtService.generateRefreshToken(userDetails, user.getId());

        refreshTokenService.save(user, refreshToken, jwtService.getRefreshExpirationSeconds());

        response.addHeader(
                "Set-Cookie",
                authCookieService.createAccessTokenCookie(
                        accessToken,
                        jwtService.getAccessExpirationSeconds()
                ).toString()
        );

        response.addHeader(
                "Set-Cookie",
                authCookieService.createRefreshTokenCookie(
                        refreshToken,
                        jwtService.getRefreshExpirationSeconds()
                ).toString()
        );

        response.sendRedirect(frontendUrl + "/dashboard");
    }

    private void linkProvider(User user,
                              AuthProvider providerType,
                              String providerUserId,
                              String email) {

        boolean exists = userAuthProviderRepository
                .findByProviderAndProviderUserId(providerType, providerUserId)
                .isPresent();

        if (exists) return;

        UserAuthProvider provider = new UserAuthProvider();
        provider.setUser(user);
        provider.setProvider(providerType);
        provider.setProviderUserId(providerUserId);
        provider.setProviderEmail(email);

        userAuthProviderRepository.save(provider);
    }
}