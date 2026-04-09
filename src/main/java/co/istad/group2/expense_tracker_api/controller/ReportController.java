package co.istad.group2.expense_tracker_api.controller;

import co.istad.group2.expense_tracker_api.dto.response.accountResponse.AccountSummaryResponse;
import co.istad.group2.expense_tracker_api.dto.response.goalsResponse.CashFlowResponse;
import co.istad.group2.expense_tracker_api.dto.response.categoryResponse.CategoryBreakdownResponse;
import co.istad.group2.expense_tracker_api.dto.response.dashboardResponse.MonthlySummaryResponse;
import co.istad.group2.expense_tracker_api.dto.response.dashboardResponse.TopExpensesResponse;
import co.istad.group2.expense_tracker_api.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@Slf4j
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly-summary")
    public MonthlySummaryResponse getMonthlySummary(@RequestParam String month,
                                                    Authentication authentication) {
        String email = authentication.getName();
        log.info("Get monthly summary for user={}, month={}", email, month);
        return reportService.getMonthlySummary(email, month);
    }

    @GetMapping("/category-breakdown")
    public CategoryBreakdownResponse getCategoryBreakdown(@RequestParam String month,
                                                          Authentication authentication) {
        String email = authentication.getName();
        log.info("Get category breakdown for user={}, month={}", email, month);
        return reportService.getCategoryBreakdown(email, month);
    }

    @GetMapping("/account-summary")
    public AccountSummaryResponse getAccountSummary(Authentication authentication) {
        String email = authentication.getName();
        log.info("Get account summary for user={}", email);
        return reportService.getAccountSummary(email);
    }

    @GetMapping("/cash-flow")
    public CashFlowResponse getCashFlow(@RequestParam LocalDate from,
                                        @RequestParam LocalDate to,
                                        @RequestParam(defaultValue = "DAY") String groupBy,
                                        Authentication authentication) {
        String email = authentication.getName();
        log.info("Get cash flow for user={}, from={}, to={}, groupBy={}", email, from, to, groupBy);
        return reportService.getCashFlow(email, from, to, groupBy);
    }

    @GetMapping("/top-expenses")
    public TopExpensesResponse getTopExpenses(@RequestParam String month,
                                              @RequestParam(defaultValue = "5") Integer limit,
                                              Authentication authentication) {
        String email = authentication.getName();
        log.info("Get top expenses for user={}, month={}, limit={}", email, month, limit);
        return reportService.getTopExpenses(email, month, limit);
    }
}