package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.EmfProvider;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.model.CategoryEntity;
import guru.qa.niffler.db.model.SpendEntity;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.db.Database.SPEND;

public class SpendRepositoryHibernate extends JpaService implements SpendRepository {

  public SpendRepositoryHibernate() {
    super(Map.of(
        SPEND, EmfProvider.INSTANCE.emf(SPEND).createEntityManager()
    ));
//    super(SPEND, EmfProvider.INSTANCE.emf(SPEND).createEntityManager());
  }

  @Override
  public SpendEntity createSpend(SpendEntity spendEntity) {
    persist(SPEND, spendEntity);
    return spendEntity;
  }

  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    return Optional.of(entityManager(SPEND).find(SpendEntity.class, id));
  }

  @Override
  public SpendEntity updateSpend(SpendEntity spendEntity) {
    return merge(SPEND, spendEntity);
  }

  @Override
  public void deleteSpendById(UUID id) {
    SpendEntity toBeDeleted = findSpendById(id).get();
    remove(SPEND, toBeDeleted);
  }

  @Override
  public CategoryEntity createCategory(CategoryEntity categoryEntity) {
    persist(SPEND, categoryEntity);
    return categoryEntity;
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    return Optional.of(entityManager(SPEND).find(CategoryEntity.class, id));
  }

  @Override
  public CategoryEntity updateCategory(CategoryEntity categoryEntity) {
    return merge(SPEND, categoryEntity);
  }

  @Override
  public void deleteCategoryById(UUID id) {
    CategoryEntity toBeDeleted = findCategoryById(id).get();
    remove(SPEND, toBeDeleted);
  }
}
