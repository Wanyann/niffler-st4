package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.Database;
import guru.qa.niffler.db.model.CategoryEntity;
import guru.qa.niffler.db.model.SpendEntity;
import guru.qa.niffler.db.sjdbc.CategoryEntityRowMapper;
import guru.qa.niffler.db.sjdbc.SpendEntityRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySJdbc implements SpendRepository {

  private final TransactionTemplate spendTxt;
  private final JdbcTemplate spendTemplate;

  public SpendRepositorySJdbc() {
    JdbcTransactionManager spendTm = new JdbcTransactionManager(
        DataSourceProvider.INSTANCE.dataSource(Database.SPEND)
    );

    this.spendTxt = new TransactionTemplate(spendTm);
    this.spendTemplate = new JdbcTemplate(spendTm.getDataSource());
  }

  @Override
  public SpendEntity createSpend(SpendEntity spendEntity) {
    KeyHolder kh = new GeneratedKeyHolder();
    spendTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
          "INSERT INTO \"spend\" " +
              "(username, spend_date, currency, amount, description, category_id) " +
              "VALUES (?, ?, ?, ?, ?, ?)",
          PreparedStatement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, spendEntity.getUsername());
      ps.setDate(2, new Date(spendEntity.getSpendDate().getTime()));
      ps.setString(3, spendEntity.getCurrency().name());
      ps.setDouble(4, spendEntity.getAmount());
      ps.setString(5, spendEntity.getDescription());
      ps.setObject(6, spendEntity.getCategory().getId());
      return ps;
    }, kh);

    spendEntity.setId((UUID) kh.getKeys().get("id"));

    return spendEntity;
  }

  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    try {
      return Optional.ofNullable(
          spendTemplate.queryForObject(
              "SELECT * FROM \"spend\" " +
                  "JOIN \"category\" " +
                  "ON \"spend\".category_id = \"category\".id " +
                  "WHERE \"spend\".id = ? ",
              SpendEntityRowMapper.instance,
              id
          )
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public SpendEntity updateSpend(SpendEntity spendEntity) {
    spendTemplate.update("UPDATE \"spend\"" +
            "SET username = ?, currency = ?, spend_date = ?, amount = ?, description = ?, category_id = ?" +
            "WHERE id = ?",
        spendEntity.getUsername(),
        spendEntity.getCurrency(),
        spendEntity.getSpendDate(),
        spendEntity.getAmount(),
        spendEntity.getDescription(),
        spendEntity.getCategory().getId(),
        spendEntity.getId());

    return spendEntity;
  }

  @Override
  public void deleteSpendById(UUID id) {
    spendTemplate.update("DELETE FROM \"spend\" WHERE id = ?", id);
  }

  @Override
  public CategoryEntity createCategory(CategoryEntity categoryEntity) {
    KeyHolder kh = new GeneratedKeyHolder();
    spendTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
          "INSERT INTO \"category\" " +
              "(username, category) " +
              "VALUES (?, ?)",
          PreparedStatement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, categoryEntity.getUsername());
      ps.setString(2, categoryEntity.getCategory());

      return ps;
    }, kh);

    categoryEntity.setId((UUID) kh.getKeys().get("id"));

    return categoryEntity;
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    try {
      return Optional.ofNullable(
          spendTemplate.queryForObject(
              "SELECT * FROM \"category\" WHERE id = ?",
              CategoryEntityRowMapper.instance,
              id
          )
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public CategoryEntity updateCategory(CategoryEntity categoryEntity) {
    spendTemplate.update("UPDATE \"category\"" +
            "SET category = ?, username = ?" +
            "WHERE id = ?",
        categoryEntity.getCategory(),
        categoryEntity.getUsername(),
        categoryEntity.getId());

    return categoryEntity;
  }

  @Override
  public void deleteCategoryById(UUID id) {
    spendTemplate.update("DELETE FROM \"spend\" WHERE category_id = ?", id);
    spendTemplate.update("DELETE FROM \"category\" WHERE id = ?", id);
  }
}
