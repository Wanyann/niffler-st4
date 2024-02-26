package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.Database;
import guru.qa.niffler.db.model.CategoryEntity;
import guru.qa.niffler.db.model.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositoryJdbc implements SpendRepository {

  private final DataSource spendDs = DataSourceProvider.INSTANCE.dataSource(Database.SPEND);

  @Override
  public SpendEntity createSpend(SpendEntity spendEntity) {
    try (Connection conn = spendDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement(
          "INSERT INTO \"spend\" " +
              "(username, spend_date, currency, amount, description, category_id) " +
              "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)
      ) {
        ps.setString(1, spendEntity.getUsername());
        ps.setDate(2, new Date(spendEntity.getSpendDate().getTime()));
        ps.setString(3, spendEntity.getCurrency().name());
        ps.setInt(4, spendEntity.getAmount().intValue());
        ps.setString(5, spendEntity.getDescription());
        ps.setObject(6, spendEntity.getCategory().getId());

        ps.executeUpdate();

        UUID spendId;
        try (ResultSet keys = ps.getGeneratedKeys()) {
          if (keys.next()) {
            spendId = UUID.fromString(keys.getString("id"));
          } else {
            throw new IllegalStateException("Can`t find id");
          }
        }

        spendEntity.setId(spendId);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    return spendEntity;
  }

  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    try (Connection conn = spendDs.getConnection()) {
      try (PreparedStatement spendPs = conn.prepareStatement(
          "SELECT * FROM \"spend\" " +
              "JOIN \"category\" " +
              "ON \"spend\".category_id = \"category\".id " +
              "WHERE \"spend\".id = ? ")
      ) {
        SpendEntity spend;
        CategoryEntity category;
        spendPs.setObject(1, id);

        try (ResultSet rs = spendPs.executeQuery()) {
          if (rs.next()) {
            spend = new SpendEntity();
            category = new CategoryEntity();

            // id категории
            category.setId(rs.getObject(8, UUID.class));
            category.setCategory(rs.getString("category"));
            // пользователь категории
            category.setUsername(rs.getString(10));

            // id траты
            spend.setId(rs.getObject(1, UUID.class));
            // пользователь траты
            spend.setUsername(rs.getString(2));
            spend.setSpendDate(rs.getDate("spend_date"));
            spend.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
            spend.setAmount(rs.getDouble("amount"));
            spend.setDescription(rs.getString("description"));

            spend.setCategory(category);
          } else {
            throw new RuntimeException("Spend not found");
          }
        }
        return Optional.of(spend);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public SpendEntity updateSpend(SpendEntity spendEntity) {
    try (Connection conn = spendDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement(
          "UPDATE \"spend\"" +
              "SET username = ?, currency = ?, spend_date = ?, amount = ?, description = ?, category_id = ?" +
              "WHERE id = ?"
      )) {
        ps.setString(1, spendEntity.getUsername());
        ps.setString(2, spendEntity.getCurrency().name());
        ps.setDate(3, new Date(spendEntity.getSpendDate().getTime()));
        ps.setDouble(4, spendEntity.getAmount());
        ps.setString(5, spendEntity.getDescription());
        ps.setObject(6, spendEntity.getCategory().getId());
        ps.setObject(7, spendEntity.getId());

        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return spendEntity;
  }

  @Override
  public void deleteSpendById(UUID id) {
    try (Connection conn = spendDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement(
          "DELETE FROM \"spend\"" +
              "WHERE id = ?")) {
        ps.setObject(1, id);

        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public CategoryEntity createCategory(CategoryEntity categoryEntity) {
    try (Connection conn = spendDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement(
          "INSERT INTO \"category\" " +
              "(category, username) " +
              "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)
      ) {
        ps.setString(1, categoryEntity.getCategory());
        ps.setString(2, categoryEntity.getUsername());

        ps.executeUpdate();

        UUID categoryId;
        try (ResultSet keys = ps.getGeneratedKeys()) {
          if (keys.next()) {
            categoryId = UUID.fromString(keys.getString("id"));
          } else {
            throw new IllegalStateException("Can`t find id");
          }
        }

        categoryEntity.setId(categoryId);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    return categoryEntity;
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    try (Connection conn = spendDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement(
          "SELECT * FROM \"category\"" +
              "WHERE id = (?)")
      ) {
        CategoryEntity category;

        ps.setObject(1, id);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            category = new CategoryEntity();
            category.setId(UUID.fromString(rs.getString("id")));
            category.setCategory(rs.getString("category"));
            category.setUsername(rs.getString("username"));
          } else {
            throw new RuntimeException("Category not found");
          }
        }

        return Optional.of(category);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public CategoryEntity updateCategory(CategoryEntity categoryEntity) {
    try (Connection conn = spendDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement(
          "UPDATE \"category\"" +
              "SET category = ?, username = ?" +
              "WHERE id = ?"
      )) {
        ps.setString(1, categoryEntity.getCategory());
        ps.setString(2, categoryEntity.getUsername());
        ps.setObject(3, categoryEntity.getId());

        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return categoryEntity;
  }

  @Override
  public void deleteCategoryById(UUID id) {
    try (Connection conn = spendDs.getConnection()) {
      conn.setAutoCommit(false);
      try (PreparedStatement categoryPs = conn.prepareStatement(
          "DELETE FROM \"category\"" +
              "WHERE id = ?");
           PreparedStatement spendPs = conn.prepareStatement(
               "DELETE FROM \"spend\"" +
                   "WHERE category_id = ?"
           )) {
        spendPs.setObject(1, id);
        spendPs.executeUpdate();

        categoryPs.setObject(1, id);
        categoryPs.executeUpdate();

        conn.commit();
      } catch (Exception e) {
        conn.rollback();
      } finally {
        conn.setAutoCommit(true);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
