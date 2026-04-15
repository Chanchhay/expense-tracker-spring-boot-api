package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateProfileRequest;
import co.istad.group2.expense_tracker_api.dto.response.adminResponse.UpdateUserProfileResponse;
import co.istad.group2.expense_tracker_api.dto.response.adminResponse.UserResponse;

public interface UserService {
    UserResponse getCurrentUser(String email);

    UpdateUserProfileResponse updateMyProfile(String currentUserEmail, UpdateProfileRequest request);
}