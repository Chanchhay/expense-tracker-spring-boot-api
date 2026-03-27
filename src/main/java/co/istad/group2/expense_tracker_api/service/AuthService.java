package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.dto.request.LoginRequest;
import co.istad.group2.expense_tracker_api.dto.request.RegisterRequest;
import co.istad.group2.expense_tracker_api.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}