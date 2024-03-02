package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.model.CategoryEntity;
import guru.qa.niffler.db.model.SpendEntity;
import guru.qa.niffler.db.repository.SpendRepository;
import guru.qa.niffler.db.repository.SpendRepositoryJdbc;
import guru.qa.niffler.db.repository.SpendRepositorySJdbc;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;

public class DbSpendExtension extends SpendExtension implements AfterEachCallback {

  @Override
  SpendEntity createSpend(SpendEntity spendEntity) throws IOException {
    SpendRepository spendRepository = new SpendRepositoryJdbc();

    CategoryEntity createdCategory = spendRepository.createCategory(spendEntity.getCategory());
    spendEntity.setCategory(createdCategory);

    return spendRepository.createSpend(spendEntity);
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) throws Exception {
    CategoryEntity categoryEntity = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId() + CATEGORY_KEY, CategoryEntity.class);
    SpendRepository spendRepository = new SpendRepositorySJdbc();
    spendRepository.deleteCategoryById(categoryEntity.getId());
  }

}
