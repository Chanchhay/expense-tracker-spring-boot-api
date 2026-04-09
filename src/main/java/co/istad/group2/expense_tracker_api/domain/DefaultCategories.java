package co.istad.group2.expense_tracker_api.domain;

import co.istad.group2.expense_tracker_api.domain.enums.CategoryType;
import co.istad.group2.expense_tracker_api.dto.request.createReq.DefaultCategoryData;

import java.util.List;

public final class DefaultCategories {

    private DefaultCategories() {}

    public static final List<DefaultCategoryData> ITEMS = List.of(
            new DefaultCategoryData("Food", CategoryType.EXPENSE),
            new DefaultCategoryData("Shopping", CategoryType.EXPENSE),
            new DefaultCategoryData("Transport", CategoryType.EXPENSE),
            new DefaultCategoryData("Bills", CategoryType.EXPENSE),
            new DefaultCategoryData("Entertainment", CategoryType.EXPENSE),
            new DefaultCategoryData("Salary", CategoryType.INCOME),
            new DefaultCategoryData("Bonus", CategoryType.INCOME)
    );
}
