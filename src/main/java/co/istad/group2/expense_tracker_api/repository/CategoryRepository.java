package co.istad.group2.expense_tracker_api.repository;

import co.istad.group2.expense_tracker_api.domain.Category;
import co.istad.group2.expense_tracker_api.domain.User;
import co.istad.group2.expense_tracker_api.domain.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByIdAndUserOrUserIdIsNullAndId(Integer id, User user, Integer id2);

    boolean existsByUserIdAndNameAndType(String userId, String name, CategoryType type);

    boolean existsByNameIgnoreCaseAndUserAndType(String name, User user, CategoryType type);

    @Query("""
        SELECT c
        FROM Category c
        WHERE c.isDefault = true
           OR c.user.id = :userId
        ORDER BY c.isDefault DESC, c.user.id ASC
    """)
    List<Category> findAvailableCategoriesForUser(String userId);

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
        FROM Category c
        WHERE LOWER(c.name) = LOWER(:name)
          AND c.type = :type
          AND (c.isDefault = true OR c.user.id = :userId)
    """)
    boolean existsVisibleCategoryByNameAndType(Integer userId, String name, CategoryType type);

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
        FROM Category c
        WHERE LOWER(c.name) = LOWER(:name)
          AND c.type = :type
          AND (c.isDefault = true OR c.user.id = :userId)
          AND c.id <> :categoryId
    """)
    boolean existsVisibleCategoryByNameAndTypeAndIdNot(
            String userId,
            String name,
            CategoryType type,
            Integer categoryId
    );

    Optional<Category> findByIdAndUserIdAndIsDefaultFalse(Integer id, String userId);

    @Query("""
    SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
    FROM Category c
    WHERE c.isDefault = true
      AND LOWER(c.name) = LOWER(:name)
      AND c.type = :type
""")
boolean existsDefaultByNameAndType(String name, CategoryType type);
}
