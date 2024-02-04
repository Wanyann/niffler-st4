package guru.qa.niffler.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class FriendsPage {
  private final SelenideElement mainContent = $(".main-content__section"),
      tableButtons = $(".abstract-table__buttons"),
      submitInvitationButton = $("[data-tooltip-id=\"submit-invitation\"]"),
      declineInvitationButton = $("[data-tooltip-id=\"decline-invitation\"]"),
      removeFriendButton = $("[data-tooltip-id=\"remove-friend\"]");

  private final ElementsCollection friendRows = $$(".people-content .table.abstract-table tr");

  @Step("Пользователь {userName} имеет кнопку Remove friend и текст You are friends")
  public FriendsPage userHaveRemoveFriendButton(String userName) {
    SelenideElement friendRow = friendRows.findBy(text(userName));
    step("В строке друга есть текст You are friends", () -> {
      friendRow.$(tableButtons.getSearchCriteria()).shouldHave(text("You are friends"));
    });
    step("Кнопка Remove friend в строке друга отображена", () -> {
      friendRow.$(removeFriendButton.getSearchCriteria()).shouldBe(interactable);
    });
    step("Навести курсор на кнопку Remove friend", () -> {
      friendRow.$(removeFriendButton.getSearchCriteria()).hover();
    });
    step("Тултип кнопки Remove friend отображен", () -> {
      friendRow.$("#remove-friend[role='tooltip']").shouldBe(visible).shouldHave(text("Remove friend"));
    });

    return this;
  }

  @Step("Пользователь {userName} имеет кнопки Submit invitation и Decline invitation")
  public FriendsPage userHaveSubmitAndDeclineInvitationButtons(String userName) {
    SelenideElement friendRow = friendRows.findBy(text(userName));
    step("Кнопка Submit invitation в строке пользователь отправившего предложение дружбы отображена", () -> {
      friendRow.$(submitInvitationButton.getSearchCriteria()).shouldBe(interactable);
    });
    step("Навести курсор на кнопку Submit invitation", () -> {
      friendRow.$(submitInvitationButton.getSearchCriteria()).hover();
    });
    step("Тултип кнопки Submit invitation отображен", () -> {
      friendRow.$("#submit-invitation[role='tooltip']").shouldBe(visible).shouldHave(text("Submit invitation"));
    });

    step("Кнопка Decline invitation в строке пользователь отправившего предложение дружбы отображена", () -> {
      friendRow.$(declineInvitationButton.getSearchCriteria()).shouldBe(interactable);
    });
    step("Навести курсор на кнопку Decline invitation", () -> {
      friendRow.$(declineInvitationButton.getSearchCriteria()).hover();
    });
    step("Тултип кнопки Decline invitation отображен", () -> {
      friendRow.$("#decline-invitation[role='tooltip']").shouldBe(visible).shouldHave(text("Decline invitation"));
    });

    return this;
  }

  @Step("Текст \"There are no friends yet!\" отображен в таблице")
  public FriendsPage thereAreNoFriendsTextDisplayedInTable() {
    mainContent.shouldHave(text("There are no friends yet!"));

    return this;
  }
}
