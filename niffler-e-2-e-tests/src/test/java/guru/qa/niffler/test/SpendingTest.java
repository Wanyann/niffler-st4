package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import guru.qa.niffler.jupiter.annotation.GenerateCategory;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpendingTest extends BaseWebTest {

  @BeforeEach
  void doLogin() {
    welcomePage.clickLoginButton();
    loginPage.
        authorize("duck", "12345");
  }

  static {
    Configuration.browserSize = "1980x1024";
  }

  @GenerateCategory(
      username = "duck",
      category = "Обучение"
  )
  @GenerateSpend(
      username = "duck",
      description = "QA.GURU Advanced 4",
      amount = 72500.00,
      currency = CurrencyValues.RUB
  )
  @DisabledByIssue("74")
  @Test
  void spendingShouldBeDeletedByButtonDeleteSpending(SpendJson spend) {
    mainPage
        .selectSpendingWithDescription(spend.description())
        .clickDeleteSelectedButton()
        .spendingRowsCountShouldBeEqualTo(0);
  }
}
