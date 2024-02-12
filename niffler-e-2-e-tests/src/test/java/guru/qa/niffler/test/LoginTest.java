package guru.qa.niffler.test;

import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.jupiter.annotation.DbUser;
import guru.qa.niffler.jupiter.extension.DbUserExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ExtendWith(DbUserExtension.class)
public class LoginTest extends BaseWebTest {

  @BeforeEach
  @DbUser(password = "bob123")
  void doLogin(UserAuthEntity userAuth) {
    welcomePage.clickLoginButton();
    loginPage.authorize(
        userAuth.getUsername(),
        userAuth.getPassword()
    );
  }

  @Test
  @DbUser(username = "oleg", password = "bob")
  void statisticShouldBeVisibleAfterLogin1(UserAuthEntity userAuth) {
    System.out.println(userAuth.getUsername());
    $(".main-content__section-stats").should(visible);
  }

  @Test
  void statisticShouldBeVisibleAfterLogin2() {
    $(".main-content__section-stats").should(visible);
  }

  @Test
  @DbUser
  void statisticShouldBeVisibleAfterLogin3() {
    $(".main-content__section-stats").should(visible);
  }

  @Test
  @DbUser(password = "1234")
  void statisticShouldBeVisibleAfterLogin4() {
    $(".main-content__section-stats").should(visible);
  }

  @Test
  @DbUser(username = "denis")
  void statisticShouldBeVisibleAfterLogin5() {
    $(".main-content__section-stats").should(visible);
  }

  @Test
  void statisticShouldBeVisibleAfterLogin6() {
    $(".main-content__section-stats").should(visible);
  }
}
