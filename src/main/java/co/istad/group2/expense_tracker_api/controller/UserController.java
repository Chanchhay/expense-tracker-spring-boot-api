package co.istad.group2.expense_tracker_api.controller;

import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateProfileRequest;
import co.istad.group2.expense_tracker_api.dto.response.adminResponse.UpdateUserProfileResponse;
import co.istad.group2.expense_tracker_api.dto.response.adminResponse.UserResponse;
import co.istad.group2.expense_tracker_api.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        log.info("Get current user for email={}", email);
        return userService.getCurrentUser(email);
    }

    @PatchMapping
    public UpdateUserProfileResponse updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        String email = authentication.getName();
        return userService.updateMyProfile(email, request);
    }
}