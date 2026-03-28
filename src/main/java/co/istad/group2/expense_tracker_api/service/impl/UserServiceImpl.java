package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.dto.response.UserResponse;
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
                .build();
    }
}