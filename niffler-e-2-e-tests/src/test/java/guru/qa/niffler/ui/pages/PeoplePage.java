package guru.qa.niffler.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class PeoplePage {
  private final SelenideElement tableButtons = $(".abstract-table__buttons"),
      submitInvitationButton = $("[data-tooltip-id=\"submit-invitation\"]"),
      declineInvitationButton = $("[data-tooltip-id=\"decline-invitation\"]"),
      addFriendButton = $("[data-tooltip-id=\"add-friend\"]"),
      removeFriendButton = $("[data-tooltip-id=\"remove-friend\"]");

  private final ElementsCollection friendRows = $$(".people-content .table.abstract-table tr");

  @Step("Нажать кнопку Add Friend в строке пользователя {userName}")
  public PeoplePage clickAddFriendInUserRow(String userName) {
    SelenideElement userRow = friendRows.findBy(Condition.text(userName));
    userRow.$(addFriendButton.getSearchCriteria()).click();

    return this;
  }

  @Step("Пользователь {userName} имеет Pending invitation")
  public PeoplePage userHavePendingInvitation(String userName) {
    friendRows.findBy(Condition.text(userName)).$(tableButtons.getSearchCriteria()).shouldHave(Condition.text("Pending invitation"));

    return this;
  }

  @Step("Пользователь {userName} имеет кнопку Remove friend и текст You are friends")
  public PeoplePage userHaveRemoveFriendButton(String userName) {
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
  public PeoplePage userHaveSubmitAndDeclineInvitationButtons(String userName) {
    SelenideElement friendRow = friendRows.findBy(text(userName));
    step("Кнопка Submit invitation в строке пользователя отправившего предложение дружбы отображена", () -> {
      friendRow.$(submitInvitationButton.getSearchCriteria()).shouldBe(interactable);
    });
    step("Навести курсор на кнопку Submit invitation", () -> {
      friendRow.$(submitInvitationButton.getSearchCriteria()).hover();
    });
    step("Тултип кнопки Submit invitation отображен", () -> {
      friendRow.$("#submit-invitation[role='tooltip']").shouldBe(visible).shouldHave(text("Submit invitation"));
    });

    step("Кнопка Decline invitation в строке пользователя отправившего предложение дружбы отображена", () -> {
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

  @Step("Кнопка Add friend отображена в строке пользователя {userName}")
  public PeoplePage addFriendButtonIsDisplayedInRowOfUser(String userName) {
    SelenideElement friendRow = friendRows.findBy(text(userName));
    step("Кнопка Add friend в строке пользователя отображена", () -> {
      friendRow.$(addFriendButton.getSearchCriteria()).shouldBe(interactable);
    });
    step("Навести курсор на кнопку Add friend", () -> {
      friendRow.$(addFriendButton.getSearchCriteria()).hover();
    });
    step("Тултип кнопки Add friend отображен", () -> {
      friendRow.$("#add-friend[role='tooltip']").shouldBe(visible).shouldHave(text("Add friend"));
    });

    return this;
  }
}
