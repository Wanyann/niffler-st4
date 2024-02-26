package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.model.SpendJson;

import java.io.IOException;

public abstract class SpendExtension {
  abstract SpendJson createSpend(SpendJson spendJson) throws IOException;
}
