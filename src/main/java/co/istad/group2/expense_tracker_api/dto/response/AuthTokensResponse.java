package co.istad.group2.expense_tracker_api.dto.response;

public record AuthTokensResponse(
        String accessToken,
        String refreshToken
) {
}
