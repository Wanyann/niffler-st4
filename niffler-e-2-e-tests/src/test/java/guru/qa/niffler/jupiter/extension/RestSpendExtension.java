package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.jupiter.annotation.DbUser;
import guru.qa.niffler.jupiter.annotation.GenerateCategory;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

public class RestSpendExtension extends SpendExtension implements BeforeEachCallback {

  public static final ExtensionContext.Namespace NAMESPACE
      = ExtensionContext.Namespace.create(RestSpendExtension.class);

  // todo move to separate client class
  private static final OkHttpClient httpClient = new OkHttpClient.Builder().build();
  private static final Retrofit retrofit = new Retrofit.Builder()
      .client(httpClient)
      .baseUrl("http://127.0.0.1:8093")
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final SpendApi spendApi = retrofit.create(SpendApi.class);

  @Override
  public void beforeEach(ExtensionContext extensionContext) throws IOException {
    Optional<GenerateSpend> spend = AnnotationSupport.findAnnotation(
        extensionContext.getRequiredTestMethod(),
        GenerateSpend.class
    );

    Optional<GenerateCategory> categoryAnnotation = AnnotationSupport.findAnnotation(
        extensionContext.getRequiredTestMethod(),
        GenerateCategory.class
    );

    Optional<DbUser> dbUserAnnotation = AnnotationSupport.findAnnotation(
        extensionContext.getRequiredTestMethod(),
        DbUser.class
    );

    if (spend.isPresent()) {
      GenerateSpend spendData = spend.get();

      String category = spendData.category().equals("unassigned") && categoryAnnotation.isPresent() ?
          categoryAnnotation.get().category() :
          spendData.category();

      String username = spendData.username().equals("unassigned") && dbUserAnnotation.isPresent() ?
          dbUserAnnotation.get().username() :
          spendData.username();

      if (category.equals("unassigned")) {
        throw new RuntimeException("Category not found. Please provide category value.");
      } else if (username.equals("unassigned")) {
        throw new RuntimeException("Username not found. Please provide username value.");
      }

      SpendJson spendJson = new SpendJson(
          null,
          new Date(),
          new CategoryJson(null, category, null),
          spendData.currency(),
          spendData.amount(),
          spendData.description(),
          username
      );

      extensionContext.getStore(NAMESPACE)
          .put("spend", createSpend(spendJson));
    }
  }

  @Override
  SpendJson createSpend(SpendJson spendJson) throws IOException {
    return spendApi.addSpend(spendJson).execute().body();
  }
  // todo проверить что работает, и перед тестом есть пользователь
}
