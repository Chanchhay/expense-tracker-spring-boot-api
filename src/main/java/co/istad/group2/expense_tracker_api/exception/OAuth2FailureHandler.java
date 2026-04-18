package co.istad.group2.expense_tracker_api.exception;

import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                        @NonNull AuthenticationException exception) throws IOException {

        String errorCode = "oauth2_error";

        // If the account is inactive, our CustomUserDetailsService threw this!
        if (exception instanceof DisabledException || exception.getCause() instanceof DisabledException) {
            errorCode = "account_disabled";
        }

        String frontendLoginUrl = "http://localhost:3000/login";
        String targetUrl = UriComponentsBuilder.fromUriString(frontendLoginUrl)
                .queryParam("error", errorCode)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}