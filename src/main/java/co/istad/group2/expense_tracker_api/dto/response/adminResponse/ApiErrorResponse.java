package co.istad.group2.expense_tracker_api.dto.response.adminResponse;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ApiErrorResponse(LocalDateTime timestamp,
                               int status,
                               String error,
                               String message,
                               String path,
                               List<String> details) {

}