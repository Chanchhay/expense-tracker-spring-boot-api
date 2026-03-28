package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.dto.request.LoginRequest;
import co.istad.group2.expense_tracker_api.dto.request.RegisterRequest;

public interface AuthService {
    String register(RegisterRequest request);
    String login(LoginRequest request);
}