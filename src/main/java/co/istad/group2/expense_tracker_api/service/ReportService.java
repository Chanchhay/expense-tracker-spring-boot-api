package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.dto.response.accountResponse.AccountSummaryResponse;
import co.istad.group2.expense_tracker_api.dto.response.categoryResponse.CategoryBreakdownResponse;
import co.istad.group2.expense_tracker_api.dto.response.dashboardResponse.MonthlySummaryResponse;
import co.istad.group2.expense_tracker_api.dto.response.dashboardResponse.TopExpensesResponse;
import co.istad.group2.expense_tracker_api.dto.response.goalsResponse.CashFlowResponse;

import java.time.LocalDate;

public interface ReportService {
    MonthlySummaryResponse getMonthlySummary(String email, String month);

    CategoryBreakdownResponse getCategoryBreakdown(String email, String month);

    AccountSummaryResponse getAccountSummary(String email);

    CashFlowResponse getCashFlow(String email, LocalDate from, LocalDate to, String groupBy);

    TopExpensesResponse getTopExpenses(String email, String month, Integer limit);
}