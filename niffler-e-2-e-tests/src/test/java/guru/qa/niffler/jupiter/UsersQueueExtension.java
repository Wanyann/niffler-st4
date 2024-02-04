package guru.qa.niffler.jupiter;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static guru.qa.niffler.jupiter.User.UserType.*;

public class UsersQueueExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE
      = ExtensionContext.Namespace.create(UsersQueueExtension.class);

  private static final Map<User.UserType, Queue<UserJson>> users = new ConcurrentHashMap<>();

  static {
    Queue<UserJson> friendsQueue = new ConcurrentLinkedQueue<>();
    Queue<UserJson> commonQueue = new ConcurrentLinkedQueue<>();
    Queue<UserJson> invitationSendQueue = new ConcurrentLinkedQueue<>();
    Queue<UserJson> invitationReceivedQueue = new ConcurrentLinkedQueue<>();
    friendsQueue.add(user("duck", "12345", WITH_FRIENDS));
    friendsQueue.add(user("dima", "12345", WITH_FRIENDS));
    commonQueue.add(user("bee", "12345", COMMON));
    commonQueue.add(user("barsik", "12345", COMMON));
    invitationSendQueue.add(user("test", "12345", INVITATION_SEND));
    invitationReceivedQueue.add(user("guy", "12345", INVITATION_RECEIVED));
    users.put(WITH_FRIENDS, friendsQueue);
    users.put(COMMON, commonQueue);
    users.put(INVITATION_SEND, invitationSendQueue);
    users.put(INVITATION_RECEIVED, invitationReceivedQueue);
  }

  private static UserJson user(String username, String password, User.UserType userType) {
    return new UserJson(
        null,
        username,
        null,
        null,
        CurrencyValues.RUB,
        null,
        null,
        new TestData(
            password,
            userType
        )
    );
  }

  // todo смержить данные о параметрах before each и самого тестового метода. понять, как выполняется before each extension с тестовым методом
  // todo и методом before each. before extension выполняется единожды перед тестом.
  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    System.out.println("\n\n######## Before start");
    System.out.println("############ Выполняю before ext для метода " + context.getRequiredTestMethod() + ". id " + context.getUniqueId());

    Parameter[] parameters = context.getRequiredTestMethod().getParameters();

    Method[] declaredMethods = context.getRequiredTestClass().getDeclaredMethods();
    for (Method declaredMethod : declaredMethods) {
      if (declaredMethod.isAnnotationPresent(BeforeEach.class)) {
        Parameter[] methodParameters = parameters.clone();
        Parameter[] beforeParameters = declaredMethod.getParameters();
        parameters = new Parameter[methodParameters.length + beforeParameters.length];
        System.arraycopy(methodParameters, 0, parameters, 0, methodParameters.length);
        System.arraycopy(beforeParameters, 0, parameters, methodParameters.length, beforeParameters.length);
        break;
      }
    }

    System.out.println("Параметры после проверки " + Arrays.toString(parameters));

    for (Parameter parameter : parameters) {
      User annotation = parameter.getAnnotation(User.class);
      System.out.println("try to find annotation " + parameter);
      if (annotation != null && parameter.getType().isAssignableFrom(UserJson.class)) {
        System.out.println("annotation found " + annotation);
        UserJson testCandidate = null;
        Queue<UserJson> queue = users.get(annotation.value());
        while (testCandidate == null) {
          testCandidate = queue.poll();
        }
        System.out.println("ключ " + context.getUniqueId() + parameter.getName() + parameter.getDeclaringExecutable().getName());
        System.out.println("беру " + testCandidate);
        context.getStore(NAMESPACE).put(context.getUniqueId() + parameter.getName() + parameter.getDeclaringExecutable().getName(), testCandidate);
      }
    }
    System.out.println("Before");
    users.forEach((key, value) -> System.out.println(key + " " + value));
    System.out.println("####### Before end");
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    System.out.println("\n\n######## After start");
    Parameter[] parameters = context.getRequiredTestMethod().getParameters();

    Method[] declaredMethods = context.getRequiredTestClass().getDeclaredMethods();
    for (Method declaredMethod : declaredMethods) {
      if (declaredMethod.isAnnotationPresent(BeforeEach.class)) {
        Parameter[] methodParameters = parameters.clone();
        Parameter[] beforeParameters = declaredMethod.getParameters();
        parameters = new Parameter[methodParameters.length + beforeParameters.length];
        System.arraycopy(methodParameters, 0, parameters, 0, methodParameters.length);
        System.arraycopy(beforeParameters, 0, parameters, methodParameters.length, beforeParameters.length);
        break;
      }
    }

    for (Parameter parameter : parameters) {
      User annotation = parameter.getAnnotation(User.class);
      System.out.println("\n##### check test params");
      System.out.println("try to find annotation " + parameter);
      if (annotation != null && parameter.getType().isAssignableFrom(UserJson.class)) {
        System.out.println("annotation found " + annotation);

        UserJson userFromTest = context.getStore(NAMESPACE)
            .get(context.getUniqueId() + parameter.getName() + parameter.getDeclaringExecutable().getName(), UserJson.class);
        System.out.println("возвращаю " + userFromTest);
        users.get(userFromTest.testData().userType()).add(userFromTest);
      }
    }

    System.out.println("\n After");
    users.forEach((key, value) -> System.out.println(key + " " + value));

  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter()
        .getType()
        .isAssignableFrom(UserJson.class) &&
        parameterContext.getParameter().isAnnotationPresent(User.class);
  }

  @Override
  public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    System.out.println(parameterContext.getParameter());
    return extensionContext.getStore(NAMESPACE)
        .get(extensionContext.getUniqueId() +
            parameterContext.getParameter().getName() +
            parameterContext.getParameter().getDeclaringExecutable().getName(), UserJson.class);
  }
}
