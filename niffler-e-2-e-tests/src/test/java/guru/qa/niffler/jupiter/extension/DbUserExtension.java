package guru.qa.niffler.jupiter.extension;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositoryJdbc;
import guru.qa.niffler.jupiter.annotation.DbUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;
import java.util.*;

public class DbUserExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DbUserExtension.class);
  public static final String USER_KEY = "user";
  public static final String USER_ID = "userId";
  public static final String AUTH_ID = "authId";
  static final Faker FAKER = new Faker();
  UserRepository userRepository = new UserRepositoryJdbc();

  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception {
    List<Method> requiredMethods = new ArrayList<>();
    requiredMethods.add(extensionContext.getRequiredTestMethod());

    Arrays.stream(extensionContext.getRequiredTestClass().getDeclaredMethods())
        .filter(method -> method.isAnnotationPresent(BeforeEach.class))
        .filter(method -> method.isAnnotationPresent(DbUser.class))
        .forEach(requiredMethods::add);
    UserAuthEntity userAuth = new UserAuthEntity();
    UserEntity user = new UserEntity();

    boolean passLocked = false;
    boolean usernameLocked = false;

    for (Method method : requiredMethods) {
      Optional<DbUser> dbUser = AnnotationSupport.findAnnotation(
          method,
          DbUser.class
      );

      if (dbUser.isPresent()) {
        DbUser userData = dbUser.get();

        if (!userData.username().isEmpty() && !usernameLocked) {
          userAuth.setUsername(userData.username());
          user.setUsername(userData.username());
          if (method.equals(extensionContext.getRequiredTestMethod())) {
            usernameLocked = true;
          }
        }
        if (!userData.password().isEmpty() && !passLocked) {
          userAuth.setPassword(userData.password());
          if (method.equals(extensionContext.getRequiredTestMethod())) {
            passLocked = true;
          }
        }
      }
    }
    if (userAuth.getUsername() == null) {
      userAuth.setUsername(FAKER.name().name());
      user.setUsername(userAuth.getUsername());
    }
    if (userAuth.getPassword() == null) {
      userAuth.setPassword(FAKER.number().digits(5));
    }

    userAuth.setEnabled(true);
    userAuth.setAccountNonExpired(true);
    userAuth.setAccountNonLocked(true);
    userAuth.setCredentialsNonExpired(true);
    userAuth.setAuthorities(Arrays.stream(Authority.values())
        .map(e -> {
          AuthorityEntity ae = new AuthorityEntity();
          ae.setAuthority(e);
          return ae;
        }).toList()
    );
    user.setCurrency(CurrencyValues.RUB);

    HashMap<String, UUID> ids = new HashMap<>();
    UserAuthEntity userAuthEntity = userRepository.createInAuth(userAuth);
    ids.put(AUTH_ID, userAuthEntity.getId());
    UserEntity userEntity = userRepository.createInUserdata(user);
    ids.put(USER_ID, userEntity.getId());

    extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), ids);
    extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId() + USER_KEY, userAuthEntity);
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) {
    HashMap<String, UUID> ids = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), HashMap.class);
    userRepository.deleteInAuthById(ids.get(AUTH_ID));
    userRepository.deleteInUserdataById(ids.get(USER_ID));
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter()
        .getType()
        .isAssignableFrom(UserAuthEntity.class);
  }

  @Override
  public UserAuthEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(DbUserExtension.NAMESPACE)
        .get(extensionContext.getUniqueId() + USER_KEY, UserAuthEntity.class);
  }
}
