package co.istad.group2.expense_tracker_api.service;

import co.istad.group2.expense_tracker_api.dto.response.DashboardResponse;

public interface DashboardService {
    DashboardResponse getDashboardSummary(String email);
}