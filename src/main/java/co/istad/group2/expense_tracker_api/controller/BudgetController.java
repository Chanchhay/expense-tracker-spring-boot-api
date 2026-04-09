package co.istad.group2.expense_tracker_api.controller;

import co.istad.group2.expense_tracker_api.dto.request.createReq.CreateBudgetRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateBudgetRequest;
import co.istad.group2.expense_tracker_api.dto.response.budgetResponse.BudgetResponse;
import co.istad.group2.expense_tracker_api.dto.response.budgetResponse.BudgetSummaryResponse;
import co.istad.group2.expense_tracker_api.service.BudgetService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
@Slf4j
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BudgetResponse createBudget(@Valid @RequestBody CreateBudgetRequest request,
                                       Authentication authentication) {
        String email = authentication.getName();
        log.info("Create budget for user={}", email);
        return budgetService.createBudget(request, email);
    }

    @GetMapping
    public List<BudgetResponse> getMyBudgets(Authentication authentication) {
        String email = authentication.getName();
        log.info("Get budgets for user={}", email);
        return budgetService.getMyBudgets(email);
    }

    @GetMapping("/{id}")
    public BudgetResponse getBudgetById(@PathVariable String id,
                                        Authentication authentication) {
        String email = authentication.getName();
        log.info("Get budget id={} for user={}", id, email);
        return budgetService.getBudgetById(id, email);
    }

    @PutMapping("/{id}")
    public BudgetResponse updateBudget(@PathVariable String id,
                                       @Valid @RequestBody UpdateBudgetRequest request,
                                       Authentication authentication) {
        String email = authentication.getName();
        log.info("Update budget id={} for user={}", id, email);
        return budgetService.updateBudget(id, request, email);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteBudget(@PathVariable String id,
                             Authentication authentication) {
        String email = authentication.getName();
        log.info("Delete budget id={} for user={}", id, email);
        budgetService.deleteBudget(id, email);
    }

    @GetMapping("/summary")
    public BudgetSummaryResponse getBudgetSummary(@RequestParam String month,
                                                  Authentication authentication) {
        String email = authentication.getName();
        log.info("Get budget summary for user={}, month={}", email, month);
        return budgetService.getBudgetSummary(email, month);
    }
}