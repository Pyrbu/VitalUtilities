package me.pyr.utilities.storage.implementations;

import me.pyr.utilities.UtilitiesPlugin;
import me.pyr.utilities.storage.UtilitiesStorageProvider;
import me.pyr.utilities.storage.UtilitiesUser;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MongoDBStorage implements UtilitiesStorageProvider {

    // TODO: MongoDB storage

    private final UtilitiesPlugin plugin;

    public MongoDBStorage(UtilitiesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<UtilitiesUser> getUser(UUID uuid) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void updateUser(UtilitiesUser user) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void shutdown() {
        throw new RuntimeException("Not implemented");
    }

}
