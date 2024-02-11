package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.db.model.UserEntity;

import java.util.UUID;

public interface UserRepository {

  UserAuthEntity createInAuth(UserAuthEntity user);

  UserEntity createInUserdata(UserEntity user);

  void deleteInAuthById(UUID id);

  void deleteInUserdataById(UUID id);

  UserEntity getInUserdataById(UUID id);

  UserAuthEntity getInAuthById(UUID id);

  UserEntity updateInUserdataById(UserEntity user);

  UserAuthEntity updateInAuthById(UserAuthEntity user);
}
