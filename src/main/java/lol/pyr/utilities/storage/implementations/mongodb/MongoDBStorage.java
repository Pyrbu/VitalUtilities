package lol.pyr.utilities.storage.implementations.mongodb;

import lol.pyr.utilities.UtilitiesPlugin;
import lol.pyr.utilities.storage.StorageImplementationProvider;
import lol.pyr.utilities.storage.model.User;

import java.util.UUID;

public class MongoDBStorage implements StorageImplementationProvider {

    // TODO: MongoDB storage

    private final UtilitiesPlugin plugin;

    public MongoDBStorage(UtilitiesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public User loadUser(UUID uuid) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void saveUser(User user) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void shutdown() {
        throw new RuntimeException("Not implemented");
    }

}
