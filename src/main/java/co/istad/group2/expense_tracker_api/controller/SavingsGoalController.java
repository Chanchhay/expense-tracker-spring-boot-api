package co.istad.group2.expense_tracker_api.controller;

import co.istad.group2.expense_tracker_api.dto.request.createReq.CreateSavingsGoalRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateSavingsGoalProgressRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateSavingsGoalRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateSavingsGoalStatusRequest;
import co.istad.group2.expense_tracker_api.dto.response.goalsResponse.SavingsGoalResponse;
import co.istad.group2.expense_tracker_api.service.SavingsGoalService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goals")
@Slf4j
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public SavingsGoalResponse createSavingsGoal(@Valid @RequestBody CreateSavingsGoalRequest request,
                                                 Authentication authentication) {
        String email = authentication.getName();
        log.info("Create savings goal for user={}", email);
        return savingsGoalService.createSavingsGoal(request, email);
    }

    @GetMapping
    public List<SavingsGoalResponse> getMySavingsGoals(Authentication authentication) {
        String email = authentication.getName();
        log.info("Get savings goals for user={}", email);
        return savingsGoalService.getMySavingsGoals(email);
    }

    @GetMapping("/{id}")
    public SavingsGoalResponse getSavingsGoalById(@PathVariable String id,
                                                  Authentication authentication) {
        String email = authentication.getName();
        log.info("Get savings goal id={} for user={}", id, email);
        return savingsGoalService.getSavingsGoalById(id, email);
    }

    @PutMapping("/{id}")
    public SavingsGoalResponse updateSavingsGoal(@PathVariable String id,
                                                 @Valid @RequestBody UpdateSavingsGoalRequest request,
                                                 Authentication authentication) {
        String email = authentication.getName();
        log.info("Update savings goal id={} for user={}", id, email);
        return savingsGoalService.updateSavingsGoal(id, request, email);
    }

    @PatchMapping("/{id}/progress")
    public SavingsGoalResponse updateSavingsGoalProgress(
            @PathVariable String id,
            @Valid @RequestBody UpdateSavingsGoalProgressRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        log.info("Update savings goal progress id={} for user={}", id, email);
        return savingsGoalService.updateSavingsGoalProgress(id, request, email);
    }

    @PatchMapping("/{id}/status")
    public SavingsGoalResponse updateSavingsGoalStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateSavingsGoalStatusRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        log.info("Update savings goal status id={} for user={}", id, email);
        return savingsGoalService.updateSavingsGoalStatus(id, request, email);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteSavingsGoal(@PathVariable String id,
                                  Authentication authentication) {
        String email = authentication.getName();
        log.info("Delete savings goal id={} for user={}", id, email);
        savingsGoalService.deleteSavingsGoal(id, email);
    }
}