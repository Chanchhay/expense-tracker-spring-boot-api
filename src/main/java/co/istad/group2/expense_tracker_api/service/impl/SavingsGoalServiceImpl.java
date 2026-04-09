package co.istad.group2.expense_tracker_api.service.impl;

import co.istad.group2.expense_tracker_api.domain.SavingsGoal;
import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.domain.enums.GoalStatus;
import co.istad.group2.expense_tracker_api.dto.request.createReq.CreateSavingsGoalRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateSavingsGoalProgressRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateSavingsGoalRequest;
import co.istad.group2.expense_tracker_api.dto.request.updateReq.UpdateSavingsGoalStatusRequest;
import co.istad.group2.expense_tracker_api.dto.response.goalsResponse.SavingsGoalResponse;
import co.istad.group2.expense_tracker_api.exception.BadRequestException;
import co.istad.group2.expense_tracker_api.exception.NotFoundException;
import co.istad.group2.expense_tracker_api.repository.SavingsGoalRepository;
import co.istad.group2.expense_tracker_api.repository.UserRepository;
import co.istad.group2.expense_tracker_api.service.SavingsGoalService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;

    public SavingsGoalServiceImpl(SavingsGoalRepository savingsGoalRepository,
                                  UserRepository userRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.userRepository = userRepository;
    }

    @Override
    public SavingsGoalResponse createSavingsGoal(CreateSavingsGoalRequest request, String email) {
        validateDeadline(request.deadline());

        User user = getUserByEmail(email);

        SavingsGoal goal = new SavingsGoal();
        goal.setName(request.name().trim().replaceAll("\\s+", " "));
        goal.setTargetAmount(request.targetAmount());
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setDeadline(request.deadline());
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setUser(user);

        SavingsGoal savedGoal = savingsGoalRepository.save(goal);
        return mapToResponse(savedGoal);
    }

    @Override
    public List<SavingsGoalResponse> getMySavingsGoals(String email) {
        User user = getUserByEmail(email);

        return savingsGoalRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SavingsGoalResponse getSavingsGoalById(String id, String email) {
        User user = getUserByEmail(email);

        SavingsGoal goal = savingsGoalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Savings goal not found or does not belong to user"));

        return mapToResponse(goal);
    }

    @Override
    public SavingsGoalResponse updateSavingsGoal(String id, UpdateSavingsGoalRequest request, String email) {
        validateDeadline(request.deadline());

        User user = getUserByEmail(email);

        SavingsGoal goal = savingsGoalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Savings goal not found or does not belong to user"));

        goal.setName(request.name().trim());
        goal.setTargetAmount(request.targetAmount());
        goal.setDeadline(request.deadline());

        autoAdjustGoalStatus(goal);

        SavingsGoal updatedGoal = savingsGoalRepository.save(goal);
        return mapToResponse(updatedGoal);
    }

    @Override
    public SavingsGoalResponse updateSavingsGoalProgress(String id,
                                                         UpdateSavingsGoalProgressRequest request,
                                                         String email) {
        User user = getUserByEmail(email);

        SavingsGoal goal = savingsGoalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Savings goal not found or does not belong to user"));

        goal.setCurrentAmount(request.currentAmount());
        autoAdjustGoalStatus(goal);

        SavingsGoal updatedGoal = savingsGoalRepository.save(goal);
        return mapToResponse(updatedGoal);
    }

    @Override
    public SavingsGoalResponse updateSavingsGoalStatus(String id,
                                                       UpdateSavingsGoalStatusRequest request, String email) {
        User user = getUserByEmail(email);

        SavingsGoal goal = savingsGoalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Savings goal not found or does not belong to user"));

        if (request.status() == GoalStatus.COMPLETED &&
                goal.getCurrentAmount().compareTo(goal.getTargetAmount()) < 0) {
            throw new BadRequestException("Goal cannot be marked as completed before reaching target amount");
        }

        goal.setStatus(request.status());

        SavingsGoal updatedGoal = savingsGoalRepository.save(goal);
        return mapToResponse(updatedGoal);
    }

    @Override
    public void deleteSavingsGoal(String id, String email) {
        User user = getUserByEmail(email);

        SavingsGoal goal = savingsGoalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Savings goal not found or does not belong to user"));

        savingsGoalRepository.delete(goal);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    private void validateDeadline(LocalDate deadline) {
        if (deadline != null && deadline.isBefore(LocalDate.now())) {
            throw new BadRequestException("Deadline cannot be in the past");
        }
    }

    private void autoAdjustGoalStatus(SavingsGoal goal) {
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        } else if (goal.getStatus() == GoalStatus.COMPLETED) {
            goal.setStatus(GoalStatus.ACTIVE);
        }
    }

    private SavingsGoalResponse mapToResponse(SavingsGoal goal) {
        BigDecimal remainingAmount = goal.getTargetAmount().subtract(goal.getCurrentAmount());
        if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
            remainingAmount = BigDecimal.ZERO;
        }

        BigDecimal percentageSaved = BigDecimal.ZERO;
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            percentageSaved = goal.getCurrentAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP);

            if (percentageSaved.compareTo(BigDecimal.valueOf(100)) > 0) {
                percentageSaved = BigDecimal.valueOf(100);
            }
        }

        return SavingsGoalResponse.builder()
                .id(goal.getId())
                .name(goal.getName())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .remainingAmount(remainingAmount)
                .percentageSaved(percentageSaved)
                .deadline(goal.getDeadline())
                .status(goal.getStatus())
                .createdAt(goal.getCreatedAt())
                .build();
    }
}