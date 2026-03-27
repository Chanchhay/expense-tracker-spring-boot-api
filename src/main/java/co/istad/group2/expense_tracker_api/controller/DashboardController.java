package co.istad.group2.expense_tracker_api.controller;

import co.istad.group2.expense_tracker_api.dto.response.DashboardResponse;
import co.istad.group2.expense_tracker_api.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse getDashboard(Authentication authentication) {
        String email = authentication.getName();
        log.info("Get dashboard summary for user={}", email);
        return dashboardService.getDashboardSummary(email);
    }
}