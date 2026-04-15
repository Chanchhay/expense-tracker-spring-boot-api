package co.istad.group2.expense_tracker_api.dto.response.goalsResponse;

import co.istad.group2.expense_tracker_api.domain.enums.GoalStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Builder
public record SavingsGoalResponse(
        String id,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        BigDecimal remainingAmount,
        BigDecimal percentageSaved,
        LocalDate deadline,
        GoalStatus status,
        LocalDateTime createdAt,
        String image
) {
}
