package guru.qa.niffler.jupiter.extension;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.model.CategoryEntity;
import guru.qa.niffler.db.model.SpendEntity;
import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.db.repository.SpendRepository;
import guru.qa.niffler.db.repository.SpendRepositoryHibernate;
import guru.qa.niffler.jupiter.annotation.DbUser;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

public class DbSpendExtension extends SpendExtension implements BeforeEachCallback, AfterEachCallback {
  public static final ExtensionContext.Namespace NAMESPACE
      = ExtensionContext.Namespace.create(DbSpendExtension.class);
  public static final String SPEND_KEY = "spend";
  public static final String CATEGORY_KEY = "category";
  static final Faker FAKER = new Faker();

  // todo протестить все репозитории
  SpendRepository spendRepository = new SpendRepositoryHibernate();

  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception {
    SpendEntity spendEntity = new SpendEntity();
    CategoryEntity categoryEntity = new CategoryEntity();

    Optional<GenerateSpend> spendAnnotation = AnnotationSupport.findAnnotation(
        extensionContext.getRequiredTestMethod(),
        GenerateSpend.class
    );

    Optional<DbUser> userAnnotation = AnnotationSupport.findAnnotation(
        extensionContext.getRequiredTestMethod(),
        DbUser.class
    );

    if (!userAnnotation.isPresent()) {
      throw new RuntimeException("Username wasn't provided");
    }
    if (spendAnnotation.isPresent()) {
      GenerateSpend spend = spendAnnotation.get();
      UserAuthEntity user = extensionContext.getStore(DbUserExtension.NAMESPACE)
          .get(extensionContext.getUniqueId() + DbUserExtension.USER_KEY, UserAuthEntity.class);
      categoryEntity.setUsername(user.getUsername());

      if (spend.description().equals("unassigned")) {
        spendEntity.setDescription(FAKER.book().title());
      } else {
        spendEntity.setDescription(spend.category());
      }

      if (spend.category().equals("unassigned")) {
        categoryEntity.setCategory(FAKER.cat().name());
      } else {
        categoryEntity.setCategory(spend.category());
      }

      if (spend.amount() == 0) {
        spendEntity.setAmount(FAKER.number().randomDouble(5, 1, 100_000));
      } else {
        spendEntity.setAmount(spend.amount());
      }

      spendEntity.setSpendDate(new Date());
      spendEntity.setCurrency(spend.currency());
      spendEntity.setCategory(categoryEntity);
      spendEntity.setUsername(user.getUsername());

      SpendJson createdSpend = createSpend(spendEntity.toJson());

      extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId() + SPEND_KEY, createdSpend);
      extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId() + CATEGORY_KEY, createdSpend.category());
    }
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) throws Exception {
    CategoryJson categoryJson = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId() + CATEGORY_KEY, CategoryJson.class);
    spendRepository.deleteCategoryById(categoryJson.id());
  }

  @Override
  SpendJson createSpend(SpendJson spendJson) throws IOException {
    CategoryEntity categoryToCreate = spendJson.category().toEntity();

    CategoryEntity createdCategory = spendRepository.createCategory(categoryToCreate);

    SpendEntity spendToCreate = spendJson.toEntity();
    spendToCreate.setCategory(createdCategory);

    SpendEntity createdSpend = spendRepository.createSpend(spendToCreate);

    return createdSpend.toJson();
  }
}
