package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.dto.request.RegisterRequest;
import co.istad.group2.expense_tracker_api.dto.request.UserRequest;
import co.istad.group2.expense_tracker_api.dto.response.UserResponse;

public interface UserService {
    UserResponse create(RegisterRequest registerRequest);
}
