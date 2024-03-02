package guru.qa.niffler.db.sjdbc;

import guru.qa.niffler.db.model.CategoryEntity;
import guru.qa.niffler.db.model.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SpendEntityRowMapper implements RowMapper<SpendEntity> {

  public static final SpendEntityRowMapper instance = new SpendEntityRowMapper();

  @Override
  public SpendEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    SpendEntity spend = new SpendEntity();
    CategoryEntity category = new CategoryEntity();

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

    return spend;
  }
}
