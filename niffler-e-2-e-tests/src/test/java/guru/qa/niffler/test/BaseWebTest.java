package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.ui.pages.LoginPage;
import guru.qa.niffler.ui.pages.MainPage;
import guru.qa.niffler.ui.pages.PeoplePage;
import guru.qa.niffler.ui.pages.WelcomePage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({BrowserExtension.class})
public abstract class BaseWebTest {
  public MainPage mainPage = new MainPage();
  public LoginPage loginPage = new LoginPage();
  public WelcomePage welcomePage = new WelcomePage();

  public PeoplePage peoplePage = new PeoplePage();

  @BeforeAll
  static void beforeAll() {
    Configuration.browserSize = "1920x1080";
  }

  @BeforeEach
  void openPage() {
    Selenide.open("http://127.0.0.1:3000/main");
  }
}
