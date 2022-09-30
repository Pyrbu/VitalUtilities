package me.pyr.utilities.storage;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UtilitiesStorageProvider {

    CompletableFuture<UtilitiesUser> getUser(UUID uuid);
    void updateUser(UtilitiesUser user);
    void shutdown();

}
