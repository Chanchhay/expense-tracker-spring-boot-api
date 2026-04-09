package co.istad.group2.expense_tracker_api.dto.request.createReq;

import co.istad.group2.expense_tracker_api.domain.enums.CategoryType;

public record DefaultCategoryData(String name, CategoryType type) {}
