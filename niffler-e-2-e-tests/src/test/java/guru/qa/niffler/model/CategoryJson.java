package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.db.model.CategoryEntity;

import java.util.UUID;

public record CategoryJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("category")
    String category,
    @JsonProperty("username")
    String username) {
  public CategoryEntity toEntity() {
    CategoryEntity categoryEntity = new CategoryEntity();

    categoryEntity.setId(this.id());
    categoryEntity.setCategory(this.category());
    categoryEntity.setUsername(this.username());

    return categoryEntity;
  }
}