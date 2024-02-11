package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.jupiter.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.User.UserType.*;

@ExtendWith(UsersQueueExtension.class)
public class FriendsTest extends BaseWebTest {

  @BeforeEach
  void doLogin() {
    welcomePage.clickLoginButton();
  }

  @Test
  @DisplayName("Пользователь, получивший предложение дружбы, на вкладке Friends имеет кнопки Decline invitation и Accept invitation в строке отправителя")
  void userReceivedInvitationHaveSubmitAndDeclineButtonsInFriendsPage(@User(INVITATION_SEND) UserJson user, @User(INVITATION_RECEIVED) UserJson pendingUser) {
    loginPage
        .authorize(pendingUser.username(), pendingUser.testData().password());

    mainPage.openFriendsPage().userHaveSubmitAndDeclineInvitationButtons(user.username());
  }

  @Test
  @DisplayName("Пользователь, получивший предложение дружбы, на вкладке People имеет кнопки Decline invitation и Accept invitation в строке отправителя")
  void userReceivedInvitationHaveSubmitAndDeclineButtons(@User(INVITATION_SEND) UserJson user, @User(INVITATION_RECEIVED) UserJson pendingUser) {
    loginPage
        .authorize(pendingUser.username(), pendingUser.testData().password());

    mainPage.openPeoplePage().userHaveSubmitAndDeclineInvitationButtons(user.username());
  }

  @Test
  @Disabled("Доработать, добавив функционал создания пользователей для теста")
  @DisplayName("При отправке приглашения пользователю кнопка кнопка Add Friend заменяется на текст \"Pending Invitation\"")
  void sendInvitationToUserTest(@User(COMMON) UserJson user, @User(COMMON) UserJson secondUser) {
    loginPage
        .authorize(user.username(), user.testData().password());

    mainPage.openPeoplePage().clickAddFriendInUserRow(secondUser.surname());
  }

  @Test
  @DisplayName("Пользователь, которому отправлено приглашение отображен в списке всех пользователей")
  void usersWithPendingDisplayedInPeople(@User(INVITATION_SEND) UserJson user, @User(INVITATION_RECEIVED) UserJson pendingUser) {
    loginPage.authorize(user.username(), user.testData().password());

    mainPage.openPeoplePage();
    peoplePage.userHavePendingInvitation(pendingUser.username());
  }

  @Test
  @DisplayName("Пользователь с другом имеет кнопку Remove friend в строке друга на странице Friends")
  void userWithFriendHaveButtonRemoveFriendInFriendsPage(@User(WITH_FRIENDS) UserJson firstUser, @User(WITH_FRIENDS) UserJson secondUser) {
    loginPage.authorize(firstUser.username(), firstUser.testData().password());

    mainPage.openFriendsPage()
        .userHaveRemoveFriendButton(secondUser.username());
  }

  @Test
  @DisplayName("Пользователь с другом имеет кнопку Remove friend в строке друга на странице People")
  void userWithFriendHaveButtonRemoveFriendInPeoplePage(@User(WITH_FRIENDS) UserJson firstUser, @User(WITH_FRIENDS) UserJson secondUser) {
    loginPage.authorize(firstUser.username(), firstUser.testData().password());

    mainPage.openPeoplePage()
        .userHaveRemoveFriendButton(secondUser.username());
  }

  @Test
  @DisplayName("Пользователь с входящим приглашением в друзья имеет красный индикатор рядом с кнопкой Friends")
  void userWithPendingInvitationShouldHaveRedDotIndicatorNearToFriendsButton(@User(INVITATION_RECEIVED) UserJson user) {
    loginPage.authorize(user.username(), user.testData().password());

    mainPage.friendsButtonHaveRedIndicatorOfIncomingInvitation();
  }

  @Test
  @DisplayName("У пользователя без друзей в таблице друзей отображается текст \"There are no friends yet!\"")
  void userWithoutFriendsHaveTextInFriendsTable(@User(COMMON) UserJson user) {
    loginPage.authorize(user.username(), user.testData().password());

    mainPage.openFriendsPage().thereAreNoFriendsTextDisplayedInTable();
  }

  @Test
  @DisplayName("В строке других пользователей, не являющихся друзьями, отображена кнопка добавить друга")
  void userHaveAddFriendButtonInNonFriendUserRows(@User(COMMON) UserJson user, @User(COMMON) UserJson secondUser) {
    loginPage.authorize(user.username(), user.testData().password());

    mainPage.openPeoplePage().addFriendButtonIsDisplayedInRowOfUser(secondUser.username());
  }
}
