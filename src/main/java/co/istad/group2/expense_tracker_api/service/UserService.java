package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.dto.response.adminResponse.UserResponse;

public interface UserService {
    UserResponse getCurrentUser(String email);
}