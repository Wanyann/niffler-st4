package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.JdbcUrl;
import guru.qa.niffler.db.model.*;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryJdbc implements UserRepository {

  private final DataSource authDs = DataSourceProvider.INSTANCE.dataSource(JdbcUrl.AUTH);
  private final DataSource udDs = DataSourceProvider.INSTANCE.dataSource(JdbcUrl.USERDATA);
  private final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  @Override
  public UserAuthEntity createInAuth(UserAuthEntity user) {
    try (Connection conn = authDs.getConnection()) {
      conn.setAutoCommit(false);

      try (PreparedStatement userPs = conn.prepareStatement(
          "INSERT INTO \"user\" " +
              "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
              "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
           PreparedStatement authorityPs = conn.prepareStatement(
               "INSERT INTO \"authority\" " +
                   "(user_id, authority) " +
                   "VALUES (?, ?)")
      ) {

        userPs.setString(1, user.getUsername());
        userPs.setString(2, pe.encode(user.getPassword()));
        userPs.setBoolean(3, user.getEnabled());
        userPs.setBoolean(4, user.getAccountNonExpired());
        userPs.setBoolean(5, user.getAccountNonLocked());
        userPs.setBoolean(6, user.getCredentialsNonExpired());

        userPs.executeUpdate();

        UUID authUserId;
        try (ResultSet keys = userPs.getGeneratedKeys()) {
          if (keys.next()) {
            authUserId = UUID.fromString(keys.getString("id"));
          } else {
            throw new IllegalStateException("Can`t find id");
          }
        }

        for (Authority authority : Authority.values()) {
          authorityPs.setObject(1, authUserId);
          authorityPs.setString(2, authority.name());
          authorityPs.addBatch();
          authorityPs.clearParameters();
        }

        authorityPs.executeBatch();
        conn.commit();
        user.setId(authUserId);
      } catch (Exception e) {
        conn.rollback();
        throw e;
      } finally {
        conn.setAutoCommit(true);
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return user;
  }

  @Override
  public UserEntity createInUserdata(UserEntity user) {
    try (Connection conn = udDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement(
          "INSERT INTO \"user\" " +
              "(username, currency) " +
              "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getCurrency().name());
        ps.executeUpdate();

        UUID userId;
        try (ResultSet keys = ps.getGeneratedKeys()) {
          if (keys.next()) {
            userId = UUID.fromString(keys.getString("id"));
          } else {
            throw new IllegalStateException("Can`t find id");
          }
        }
        user.setId(userId);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return user;
  }

  @Override
  public void deleteInAuthById(UUID id) {
    try (Connection conn = authDs.getConnection()) {
      conn.setAutoCommit(false);
      try (PreparedStatement userPs = conn.prepareStatement(
          "DELETE FROM \"user\"" +
              "WHERE id=(?)");
           PreparedStatement authorityPs = conn.prepareStatement(
               "DELETE FROM \"authority\"" +
                   "WHERE user_id=(?)")
      ) {
        userPs.setObject(1, id);
        authorityPs.setObject(1, id);

        authorityPs.executeUpdate();

        userPs.executeUpdate();

        conn.commit();
      } catch (Exception e) {
        conn.rollback();
        throw e;
      } finally {
        conn.setAutoCommit(true);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteInUserdataById(UUID id) {
    try (Connection conn = udDs.getConnection()) {
      conn.setAutoCommit(false);
      try (PreparedStatement userPs = conn.prepareStatement(
          "DELETE FROM \"user\" WHERE id=(?)");
           PreparedStatement friendsPs = conn.prepareStatement(
               "DELETE FROM \"friendship\" WHERE user_id=(?)");
           PreparedStatement invitesPs = conn.prepareStatement(
               "DELETE FROM \"friendship\" WHERE friend_id=(?)")
      ) {


        friendsPs.setObject(1, id);
        friendsPs.executeUpdate();

        invitesPs.setObject(1, id);
        invitesPs.executeUpdate();

        userPs.setObject(1, id);
        userPs.executeUpdate();

        conn.commit();
      } catch (SQLException e) {
        conn.rollback();
        throw e;
      } finally {
        conn.setAutoCommit(true);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<UserEntity> getInUserdataById(UUID id) {
    try (Connection conn = udDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement(
          "SELECT * FROM \"user\"" +
              "WHERE id = (?)")
      ) {
        UserEntity user;
        ps.setObject(1, id);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            user = new UserEntity();
            user.setId(UUID.fromString(rs.getString("id")));
            user.setUsername(rs.getString("username"));
            user.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
            user.setFirstname(rs.getString("firstname"));
            user.setSurname(rs.getString("surname"));
            user.setPhoto(rs.getBytes("photo"));
            return Optional.of(user);
          } else {
            throw new RuntimeException("User not found");
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<UserAuthEntity> getInAuthById(UUID id) {
    try (Connection conn = authDs.getConnection()) {
      try (PreparedStatement userPs = conn.prepareStatement(
          "SELECT * FROM \"user\"" +
              "WHERE id = (?)");
           PreparedStatement authorityPs = conn.prepareStatement(
               "SELECT * FROM \"authority\"" +
                   "WHERE user_id = (?)")
      ) {
        UserAuthEntity userAuth;
        userPs.setObject(1, id);
        try (ResultSet rs = userPs.executeQuery()) {
          if (rs.next()) {
            userAuth = new UserAuthEntity();
            userAuth.setId(UUID.fromString(rs.getString("id")));
            userAuth.setUsername(rs.getString("username"));
            userAuth.setPassword(rs.getString("password"));
            userAuth.setEnabled(rs.getBoolean("enabled"));
            userAuth.setAccountNonExpired(rs.getBoolean("account_non_expired"));
            userAuth.setAccountNonLocked(rs.getBoolean("account_non_locked"));
            userAuth.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
          } else {
            throw new RuntimeException("User not found");
          }
        }

        authorityPs.setObject(1, id);

        try (ResultSet rs = authorityPs.executeQuery()) {
          List<AuthorityEntity> authorities = new ArrayList<>();
          while (rs.next()) {
            AuthorityEntity a = new AuthorityEntity();
            a.setId(UUID.fromString(rs.getString("id")));
            a.setAuthority(Authority.valueOf(rs.getString("authority")));
            authorities.add(a);
          }
          userAuth.setAuthorities(authorities);
        }
        return Optional.of(userAuth);
      }
    } catch (
        SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public UserEntity updateInUserdataById(UserEntity user) {
    try (Connection conn = udDs.getConnection()) {
      try (PreparedStatement userPs = conn.prepareStatement(
          "UPDATE \"user\"" +
              "SET username = (?), currency = (?), firstname = (?), surname = (?), photo = (?)" +
              "WHERE id=(?)")
      ) {
        userPs.setString(1, user.getUsername());
        userPs.setString(2, String.valueOf(user.getCurrency()));
        userPs.setString(3, user.getFirstname());
        userPs.setString(4, user.getSurname());
        userPs.setBytes(5, user.getPhoto());
        userPs.setObject(6, user.getId());

        userPs.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return user;
  }

  @Override
  public UserAuthEntity updateInAuthById(UserAuthEntity user) {
    try (Connection conn = authDs.getConnection()) {
      conn.setAutoCommit(false);
      try (PreparedStatement userPs = conn.prepareStatement(
          "UPDATE \"user\"" +
              "SET username = (?), password = (?), enabled = (?), " +
              "account_non_expired = (?), account_non_locked = (?), credentials_non_expired = (?)" +
              "WHERE id=(?)");
           PreparedStatement authorityInsertPs = conn.prepareStatement(
               "INSERT INTO \"authority\" " +
                   "(user_id, authority)" +
                   "VALUES (?, ?)");
           PreparedStatement authorityDeletePs = conn.prepareStatement(
               "DELETE FROM \"authority\"" +
                   "WHERE user_id = (?)"
           )
      ) {

        authorityDeletePs.setObject(1, user.getId());

        authorityDeletePs.executeUpdate();

        userPs.setString(1, user.getUsername());
        userPs.setString(2, pe.encode(user.getPassword()));
        userPs.setBoolean(3, user.getEnabled());
        userPs.setBoolean(4, user.getAccountNonExpired());
        userPs.setBoolean(5, user.getAccountNonLocked());
        userPs.setBoolean(6, user.getCredentialsNonExpired());
        userPs.setObject(7, user.getId());

        userPs.executeUpdate();

        for (AuthorityEntity authority : user.getAuthorities()) {
          authorityInsertPs.setObject(1, user.getId());
          authorityInsertPs.setObject(2, authority.getAuthority().toString());
          authorityInsertPs.addBatch();
          authorityInsertPs.clearParameters();
        }

        authorityInsertPs.executeBatch();

        conn.commit();
      } catch (Exception e) {
        conn.rollback();
        throw e;
      } finally {
        conn.setAutoCommit(true);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return user;
  }
}
