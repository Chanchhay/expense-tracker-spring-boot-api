package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.dto.request.createReq.LoginRequest;
import co.istad.group2.expense_tracker_api.dto.request.createReq.RegisterRequest;
import co.istad.group2.expense_tracker_api.dto.response.AuthTokensResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthTokensResponse register(RegisterRequest request);
    AuthTokensResponse login(LoginRequest request);
    AuthTokensResponse refresh(HttpServletRequest request);
    void logout(HttpServletRequest request);
}