package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.jupiter.annotation.DbUser;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.Test;

public class SpendingTest extends BaseWebTest {

  static {
    Configuration.browserSize = "1980x1024";
  }

  @DbUser
  @GenerateSpend()
  @Test
  void spendingShouldBeDeletedByButtonDeleteSpending(UserAuthEntity user, SpendJson spend) {
    welcomePage.clickLoginButton();
    loginPage.
        authorize(user.getUsername(), user.getPassword());

    mainPage
        .selectSpendingWithDescription(spend.description())
        .clickDeleteSelectedButton()
        .spendingRowsCountShouldBeEqualTo(0);
  }
}
