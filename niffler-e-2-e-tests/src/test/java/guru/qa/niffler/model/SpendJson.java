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
    CategoryJson category,
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

    categoryEntity.setId(this.category.id());
    categoryEntity.setCategory(this.category.category());
    categoryEntity.setUsername(this.category.username());

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
