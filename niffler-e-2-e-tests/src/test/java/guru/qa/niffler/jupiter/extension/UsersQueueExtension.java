package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static guru.qa.niffler.jupiter.annotation.User.UserType.*;


public class UsersQueueExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE
      = ExtensionContext.Namespace.create(UsersQueueExtension.class);

  private static final Map<User.UserType, Queue<UserJson>> USER_QUEUES = new ConcurrentHashMap<>();

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
    USER_QUEUES.put(WITH_FRIENDS, friendsQueue);
    USER_QUEUES.put(COMMON, commonQueue);
    USER_QUEUES.put(INVITATION_SEND, invitationSendQueue);
    USER_QUEUES.put(INVITATION_RECEIVED, invitationReceivedQueue);
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
  public void beforeEach(ExtensionContext context) {
    List<Method> requiredMethods = new java.util.ArrayList<>(List.of(context.getRequiredTestMethod()));
    requiredMethods.addAll(
        Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
            .filter(m -> m.isAnnotationPresent(BeforeEach.class))
            .toList());

    List<Parameter> parameters = requiredMethods.stream()
        .map(Executable::getParameters)
        .flatMap(Arrays::stream)
        .filter(parameter -> parameter.isAnnotationPresent(User.class))
        .filter(parameter -> parameter.getType().isAssignableFrom(UserJson.class))
        .toList();

    for (Parameter parameter : parameters) {
      User annotation = parameter.getAnnotation(User.class);
      UserJson testCandidate = null;
      Queue<UserJson> queue = USER_QUEUES.get(annotation.value());
      while (testCandidate == null) {
        testCandidate = queue.poll();
      }

      context.getStore(NAMESPACE).put(parameterContextKey(parameter, context), testCandidate);
    }
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    List<Method> requiredMethods = new java.util.ArrayList<>(List.of(context.getRequiredTestMethod()));
    requiredMethods.addAll(
        Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
            .filter(m -> m.isAnnotationPresent(BeforeEach.class))
            .toList());

    List<Parameter> parameters = requiredMethods.stream()
        .map(Executable::getParameters)
        .flatMap(Arrays::stream)
        .filter(parameter -> parameter.isAnnotationPresent(User.class))
        .filter(parameter -> parameter.getType().isAssignableFrom(UserJson.class))
        .toList();

    for (Parameter parameter : parameters) {
      User annotation = parameter.getAnnotation(User.class);

      UserJson userFromTest = context.getStore(NAMESPACE)
          .get(parameterContextKey(parameter, context), UserJson.class);
      USER_QUEUES.get(userFromTest.testData().userType()).add(userFromTest);
    }
  }


  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
      ParameterResolutionException {
    return parameterContext.getParameter()
        .getType()
        .isAssignableFrom(UserJson.class) &&
        parameterContext.getParameter().isAnnotationPresent(User.class);
  }

  @Override
  public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
      ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(parameterContextKey(parameterContext.getParameter(), extensionContext), UserJson.class);
  }

  private String parameterContextKey(Parameter parameter, ExtensionContext context) {
    return context.getUniqueId() + parameter.getName() + parameter.getDeclaringExecutable().getName();
  }
}
