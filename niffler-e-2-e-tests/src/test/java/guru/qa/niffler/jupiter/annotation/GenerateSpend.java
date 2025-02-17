package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.DbSpendExtension;
import guru.qa.niffler.jupiter.extension.SpendResolverExtension;
import guru.qa.niffler.model.CurrencyValues;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith({DbSpendExtension.class, SpendResolverExtension.class})
public @interface GenerateSpend {

  String username() default "unassigned";

  String description() default "unassigned";

  String category() default "unassigned";

  double amount() default 0;

  CurrencyValues currency() default CurrencyValues.RUB;
}
