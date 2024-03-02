package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.model.CategoryEntity;
import guru.qa.niffler.db.model.SpendEntity;

import java.util.Optional;
import java.util.UUID;

public interface SpendRepository {
  SpendEntity createSpend(SpendEntity spendEntity);

  Optional<SpendEntity> findSpendById(UUID id);

  SpendEntity updateSpend(SpendEntity spendEntity);

  void deleteSpendById(UUID id);

  CategoryEntity createCategory(CategoryEntity categoryEntity);

  Optional<CategoryEntity> findCategoryById(UUID id);

  CategoryEntity updateCategory(CategoryEntity categoryEntity);

  void deleteCategoryById(UUID id);
}
