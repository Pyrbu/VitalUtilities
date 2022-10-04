package lol.pyr.utilities.storage;

import lol.pyr.utilities.storage.model.User;

import java.util.UUID;

public interface StorageImplementationProvider {

    User loadUser(UUID uuid);
    void saveUser(User user);
    void shutdown();

}
