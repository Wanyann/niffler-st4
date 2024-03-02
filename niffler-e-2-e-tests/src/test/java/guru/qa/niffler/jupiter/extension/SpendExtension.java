package guru.qa.niffler.jupiter.extension;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.model.CategoryEntity;
import guru.qa.niffler.db.model.SpendEntity;
import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.jupiter.annotation.DbUser;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

public abstract class SpendExtension implements BeforeEachCallback {

  public static final ExtensionContext.Namespace NAMESPACE
      = ExtensionContext.Namespace.create(SpendExtension.class);
  public static final String SPEND_KEY = "spend";
  public static final String CATEGORY_KEY = "category";
  static final Faker FAKER = new Faker();


  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception {
    guru.qa.niffler.db.model.SpendEntity spendEntity = new SpendEntity();
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

      SpendEntity createdSpend = createSpend(spendEntity);

      extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId() + SPEND_KEY, createdSpend);
      extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId() + CATEGORY_KEY, createdSpend.getCategory());
    }
  }

  abstract SpendEntity createSpend(SpendEntity spendEntity) throws IOException;
}
