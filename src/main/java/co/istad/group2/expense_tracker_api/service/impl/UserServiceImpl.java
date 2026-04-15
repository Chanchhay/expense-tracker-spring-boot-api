package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateProfileRequest;
import co.istad.group2.expense_tracker_api.dto.response.adminResponse.UpdateUserProfileResponse;
import co.istad.group2.expense_tracker_api.dto.response.adminResponse.UserResponse;
import co.istad.group2.expense_tracker_api.exception.NotFoundException;
import co.istad.group2.expense_tracker_api.repository.UserRepository;
import co.istad.group2.expense_tracker_api.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .profile(user.getProfile())
                .build();
    }

    @Override
    public UpdateUserProfileResponse updateMyProfile(String currentUserEmail, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }

        if (request.profile() != null) {
            user.setProfile(request.profile());
        }

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    private UpdateUserProfileResponse mapToResponse(User user) {
        return UpdateUserProfileResponse.builder()
                .name(user.getName())
                .profile(user.getProfile())
                .build();
    }
}