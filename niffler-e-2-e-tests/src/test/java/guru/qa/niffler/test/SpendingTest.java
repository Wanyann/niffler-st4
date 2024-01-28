package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import guru.qa.niffler.jupiter.DisabledByIssue;
import guru.qa.niffler.jupiter.GenerateCategory;
import guru.qa.niffler.jupiter.GenerateSpend;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

public class SpendingTest extends BaseWebTest {


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
        open("http://127.0.0.1:3000/main");
        mainPage
                .selectSpendingWithDescription(spend.description())
                .clickDeleteSelectedButton()
                .spendingRowsCountShouldBeEqualTo(0);
    }
}
