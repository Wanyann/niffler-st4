package guru.qa.niffler.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {
  private final SelenideElement
      spendingsTable = $(".spendings-table"),
      spendingForm = $(".add-spending__form"),
      statisticsGraph = $(".main-content__section-stats"),
      friendsButton = $("[data-tooltip-id=\"friends\"]"),
      peopleButton = $("[data-tooltip-id=\"people\"]"),
      incomingInvitationIndicator = friendsButton.$(".header__sign");

  private final ElementsCollection
      spendingsRows = spendingsTable.$$("tbody tr"),
      spendingsButtons = $$(".spendings__table-controls button");

  private final SelenideElement
      deleteSelectedButton = spendingsButtons.findBy(text("Delete selected"));

  public MainPage checkThatMainPageSectionsAreVisible() {
    spendingsTable.shouldBe(Condition.visible);
    spendingForm.shouldBe(Condition.visible);
    statisticsGraph.shouldBe(Condition.visible);

    return this;
  }

  public MainPage selectSpendingWithDescription(String description) {
    spendingsRows.findBy(text(description)).$("td").scrollIntoView(true).click();

    return this;
  }

  public MainPage clickDeleteSelectedButton() {
    deleteSelectedButton.click();

    return this;
  }

  @Step("Над кнопкой Friends есть красный индикатор входящего приглашения")
  public MainPage friendsButtonHaveRedIndicatorOfIncomingInvitation() {
    incomingInvitationIndicator.shouldBe(visible);

    return this;
  }

  public MainPage spendingRowsCountShouldBeEqualTo(int equalTo) {
    spendingsRows.shouldHave(size(equalTo));

    return this;
  }

  @Step("Открыть Friend")
  public FriendsPage openFriendsPage() {
    friendsButton.click();

    return new FriendsPage();
  }

  @Step("Открыть People")
  public PeoplePage openPeoplePage() {
    peopleButton.click();

    return new PeoplePage();
  }


}
