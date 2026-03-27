package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.dto.request.UpdateUserRoleRequest;
import co.istad.group2.expense_tracker_api.dto.request.UpdateUserStatusRequest;
import co.istad.group2.expense_tracker_api.dto.response.AdminUserResponse;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.List;

public interface AdminUserService {
    List<AdminUserResponse> getAllUsers();
    AdminUserResponse getUserById(String id) throws ChangeSetPersister.NotFoundException;
    AdminUserResponse updateUserRole(String id, UpdateUserRoleRequest request, String currentAdminEmail);
    AdminUserResponse updateUserStatus(String id, UpdateUserStatusRequest request, String currentAdminEmail);
}