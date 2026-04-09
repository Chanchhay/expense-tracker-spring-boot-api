package co.istad.group2.expense_tracker_api.controller;

import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateUserRoleRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateUserStatusRequest;
import co.istad.group2.expense_tracker_api.dto.response.adminResponse.AdminUserResponse;
import co.istad.group2.expense_tracker_api.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public List<AdminUserResponse> getAllUsers() {
        log.info("Admin request to get all users");
        return adminUserService.getAllUsers();
    }

    @GetMapping("/{id}")
    public AdminUserResponse getUserById(@PathVariable String id) throws ChangeSetPersister.NotFoundException {
        log.info("Admin request to get user by id={}", id);
        return adminUserService.getUserById(id);
    }

    @PutMapping("/{id}/role")
    public AdminUserResponse updateUserRole(@PathVariable String id,
                                            @Valid @RequestBody UpdateUserRoleRequest request,
                                            Authentication authentication) {
        String currentAdminEmail = authentication.getName();
        log.info("Admin={} update role for user id={}", currentAdminEmail, id);
        return adminUserService.updateUserRole(id, request, currentAdminEmail);
    }

    @PatchMapping("/{id}/status")
    public AdminUserResponse updateUserStatus(@PathVariable String id,
                                              @Valid @RequestBody UpdateUserStatusRequest request,
                                              Authentication authentication) {
        String currentAdminEmail = authentication.getName();
        log.info("Admin={} update status for user id={}", currentAdminEmail, id);
        return adminUserService.updateUserStatus(id, request, currentAdminEmail);
    }
}