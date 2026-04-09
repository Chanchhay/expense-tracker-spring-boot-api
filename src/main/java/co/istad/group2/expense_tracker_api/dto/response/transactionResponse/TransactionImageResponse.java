package co.istad.group2.expense_tracker_api.dto.response.transactionResponse;

import lombok.Builder;

@Builder
public record TransactionImageResponse(String id,
                                       String imageUrl,
                                       String imagePublicId) {
}
