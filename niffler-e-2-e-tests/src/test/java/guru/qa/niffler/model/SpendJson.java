package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.db.model.CategoryEntity;
import guru.qa.niffler.db.model.SpendEntity;

import java.util.Date;
import java.util.UUID;

public record SpendJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("spendDate")
    Date spendDate,
    @JsonProperty("category")
    String category,
    @JsonProperty("currency")
    CurrencyValues currency,
    @JsonProperty("amount")
    Double amount,
    @JsonProperty("description")
    String description,
    @JsonProperty("username")
    String username) {

  public SpendEntity toEntity() {
    SpendEntity spendEntity = new SpendEntity();

    CategoryEntity categoryEntity = new CategoryEntity();
    categoryEntity.setCategory(this.category);

    spendEntity.setId(this.id);
    spendEntity.setSpendDate(this.spendDate);

    spendEntity.setCategory(categoryEntity);
    spendEntity.setCurrency(this.currency);
    spendEntity.setAmount(this.amount);
    spendEntity.setDescription(this.description);
    spendEntity.setUsername(this.username);

    return spendEntity;
  }
}
