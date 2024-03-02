package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import guru.qa.niffler.db.model.SpendEntity;
import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.jupiter.annotation.DbUser;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import org.junit.jupiter.api.Test;

public class SpendingTest extends BaseWebTest {

  static {
    Configuration.browserSize = "1980x1024";
  }

  @DbUser
  @GenerateSpend()
  @Test
  void spendingShouldBeDeletedByButtonDeleteSpending(UserAuthEntity user, SpendEntity spend) {
    welcomePage.clickLoginButton();
    loginPage.
        authorize(user.getUsername(), user.getPassword());

    mainPage
        .selectSpendingWithDescription(spend.getDescription())
//        .clickDeleteSelectedButton()
        .spendingRowsCountShouldBeEqualTo(0);
  }
}
