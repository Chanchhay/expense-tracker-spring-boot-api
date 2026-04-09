package co.istad.group2.expense_tracker_api.init;

import co.istad.group2.expense_tracker_api.domain.Category;
import co.istad.group2.expense_tracker_api.domain.enums.CategoryType;
import co.istad.group2.expense_tracker_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultCategorySeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String @NonNull ... args) {
        seed("Food", CategoryType.EXPENSE);
        seed("Transport", CategoryType.EXPENSE);
        seed("Shopping", CategoryType.EXPENSE);
        seed("Bills", CategoryType.EXPENSE);
        seed("Entertainment", CategoryType.EXPENSE);
        seed("Health", CategoryType.EXPENSE);
        seed("Salary", CategoryType.INCOME);
        seed("Bonus", CategoryType.INCOME);
        seed("Freelance", CategoryType.INCOME);
    }

    private void seed(String name, CategoryType type) {
        if (!categoryRepository.existsDefaultByNameAndType(name, type)) {
            Category category = new Category();
            category.setName(name);
            category.setType(type);
            category.setIsDefault(true);
            category.setUser(null);
            categoryRepository.save(category);
        }
    }
}