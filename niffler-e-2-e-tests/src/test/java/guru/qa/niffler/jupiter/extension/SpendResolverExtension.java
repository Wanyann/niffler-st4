package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.model.SpendEntity;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class SpendResolverExtension implements ParameterResolver {

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter()
        .getType()
        .isAssignableFrom(SpendEntity.class);
  }

  @Override
  public SpendEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(DbSpendExtension.NAMESPACE)
        .get(extensionContext.getUniqueId() + DbSpendExtension.SPEND_KEY, SpendEntity.class);
  }
}
