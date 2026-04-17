package co.istad.group2.expense_tracker_api.controller;

import co.istad.group2.expense_tracker_api.dto.request.createReq.LoginRequest;
import co.istad.group2.expense_tracker_api.dto.request.createReq.RegisterRequest;
import co.istad.group2.expense_tracker_api.dto.response.AuthTokensResponse;
import co.istad.group2.expense_tracker_api.dto.response.adminResponse.AuthResponse;
import co.istad.group2.expense_tracker_api.service.AuthCookieService;
import co.istad.group2.expense_tracker_api.service.AuthService;
import co.istad.group2.expense_tracker_api.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthCookieService authCookieService;
    private final JwtService jwtService;

    public AuthController(AuthService authService,
                          AuthCookieService authCookieService,
                          JwtService jwtService) {
        this.authService = authService;
        this.authCookieService = authCookieService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthTokensResponse tokens = authService.register(request);

        return buildAuthResponse(
                tokens,
                HttpStatus.CREATED,
                "Registered successfully"
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthTokensResponse tokens = authService.login(request);

        return buildAuthResponse(
                tokens,
                HttpStatus.OK,
                "Login successful"
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request) {
        AuthTokensResponse tokens = authService.refresh(request);

        return buildAuthResponse(
                tokens,
                HttpStatus.OK,
                "Token refreshed successfully"
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest request) {
        authService.logout(request);

        ResponseCookie accessCookie = authCookieService.clearAccessTokenCookie();
        ResponseCookie refreshCookie = authCookieService.clearRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AuthResponse("Logout successful"));
    }

    private ResponseEntity<AuthResponse> buildAuthResponse(
            AuthTokensResponse tokens,
            HttpStatus status,
            String message
    ) {
        ResponseCookie accessCookie = authCookieService.createAccessTokenCookie(
                tokens.accessToken(),
                jwtService.getAccessExpirationSeconds()
        );

        ResponseCookie refreshCookie = authCookieService.createRefreshTokenCookie(
                tokens.refreshToken(),
                jwtService.getRefreshExpirationSeconds()
        );

        return ResponseEntity.status(status)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AuthResponse(message));
    }
}