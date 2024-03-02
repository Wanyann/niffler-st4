package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.CategoryApi;
import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.db.model.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

public class RestSpendExtension extends SpendExtension implements AfterEachCallback {

  // todo move to separate client class
  private static final OkHttpClient httpClient = new OkHttpClient.Builder().build();
  private static final Retrofit retrofit = new Retrofit.Builder()
      .client(httpClient)
      .baseUrl("http://127.0.0.1:8093")
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final SpendApi spendApi = retrofit.create(SpendApi.class);
  private final CategoryApi categoryApi = retrofit.create(CategoryApi.class);

//  public void beforeEach(ExtensionContext extensionContext) throws IOException {
//    Optional<GenerateSpend> spend = AnnotationSupport.findAnnotation(
//        extensionContext.getRequiredTestMethod(),
//        GenerateSpend.class
//    );
//
//    Optional<GenerateCategory> categoryAnnotation = AnnotationSupport.findAnnotation(
//        extensionContext.getRequiredTestMethod(),
//        GenerateCategory.class
//    );
//
//    Optional<DbUser> dbUserAnnotation = AnnotationSupport.findAnnotation(
//        extensionContext.getRequiredTestMethod(),
//        DbUser.class
//    );
//
//    if (spend.isPresent()) {
//      GenerateSpend spendData = spend.get();
//
//      String category = spendData.category().equals("unassigned") && categoryAnnotation.isPresent() ?
//          categoryAnnotation.get().category() :
//          spendData.category();
//
//      String username = spendData.username().equals("unassigned") && dbUserAnnotation.isPresent() ?
//          dbUserAnnotation.get().username() :
//          spendData.username();
//
//      if (category.equals("unassigned")) {
//        throw new RuntimeException("Category not found. Please provide category value.");
//      } else if (username.equals("unassigned")) {
//        throw new RuntimeException("Username not found. Please provide username value.");
//      }
//
//      SpendJson spendJson = new SpendJson(
//          null,
//          new Date(),
//          new CategoryJson(null, category, null),
//          spendData.currency(),
//          spendData.amount(),
//          spendData.description(),
//          username
//      );
//
//      extensionContext.getStore(NAMESPACE)
//          .put("spend", createSpend(spendJson));
//    }
//  }

  @Override
  SpendEntity createSpend(SpendEntity spendEntity) throws IOException {
    CategoryJson createdCategory = categoryApi.addCategory(spendEntity.getCategory().toJson()).execute().body();

    SpendJson createdSpendJson = spendApi.addSpend(spendEntity.toJson()).execute().body();
    SpendEntity createdSpendEntity = createdSpendJson.toEntity();
    createdSpendEntity.setCategory(createdCategory.toEntity());

    return createdSpendEntity;
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) throws Exception {
    // todo не работает удаление после теста,
    //  разобраться после того как прикручу логи к ретрофиту
    SpendEntity spendEntity = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId() + SPEND_KEY, SpendEntity.class);
    spendApi.deleteSpends(spendEntity.getUsername(), List.of(spendEntity.getId().toString()));
  }

}
