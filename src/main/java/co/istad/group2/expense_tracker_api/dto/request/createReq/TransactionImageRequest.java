package co.istad.group2.expense_tracker_api.dto.request.createReq;

public record TransactionImageRequest(
        String imageUrl,
        String imagePublicId
) {
}
