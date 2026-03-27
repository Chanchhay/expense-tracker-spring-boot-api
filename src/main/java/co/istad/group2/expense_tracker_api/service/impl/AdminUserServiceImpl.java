package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.dto.request.UpdateUserRoleRequest;
import co.istad.group2.expense_tracker_api.dto.request.UpdateUserStatusRequest;
import co.istad.group2.expense_tracker_api.dto.response.AdminUserResponse;
import co.istad.group2.expense_tracker_api.exception.BadRequestException;
import co.istad.group2.expense_tracker_api.exception.NotFoundException;
import co.istad.group2.expense_tracker_api.repository.UserRepository;
import co.istad.group2.expense_tracker_api.service.AdminUserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    public AdminUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AdminUserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        return mapToResponse(user);
    }

    @Override
    public AdminUserResponse updateUserRole(String id,
                                            UpdateUserRoleRequest request,
                                            String currentAdminEmail) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        if (targetUser.getEmail().equalsIgnoreCase(currentAdminEmail)
                && !targetUser.getRole().name().equals(request.role().name())) {
            throw new BadRequestException("Admin cannot change their own role");
        }

        targetUser.setRole(request.role());
        User savedUser = userRepository.save(targetUser);

        return mapToResponse(savedUser);
    }

    @Override
    public AdminUserResponse updateUserStatus(String id,
                                              UpdateUserStatusRequest request,
                                              String currentAdminEmail) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        if (targetUser.getEmail().equalsIgnoreCase(currentAdminEmail)
                && Boolean.FALSE.equals(request.isActive())) {
            throw new BadRequestException("Admin cannot deactivate their own account");
        }

        targetUser.setIsActive(request.isActive());
        User savedUser = userRepository.save(targetUser);

        return mapToResponse(savedUser);
    }

    private AdminUserResponse mapToResponse(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}