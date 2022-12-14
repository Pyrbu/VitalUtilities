package lol.pyr.utilities.storage;

import lol.pyr.utilities.UtilitiesPlugin;
import lol.pyr.utilities.storage.implementations.MongoDBStorage;
import lol.pyr.utilities.storage.implementations.YamlStorage;
import lol.pyr.utilities.storage.model.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StorageCacheLayer implements Listener {
    private final UtilitiesPlugin plugin;
    private StorageImplementationProvider implementation;
    private final HashMap<UUID, User> userMap = new HashMap<>();
    private final BukkitTask autosaveTask;

    public StorageCacheLayer(UtilitiesPlugin plugin, StorageType type) {
        this.plugin = plugin;
        switch (type) {
            case YAML -> implementation = new YamlStorage(plugin);
            case MONGODB -> implementation = new MongoDBStorage(plugin);
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
        autosaveTask = new CacheAutosaveTask().runTask(plugin);
    }

    public CompletableFuture<User> getUser(UUID uuid) {
        CompletableFuture<User> future = new CompletableFuture<>();
        if (userMap.containsKey(uuid)) future.complete(userMap.get(uuid));
        else {
            plugin.runAsync(() -> {
                User user = implementation.loadUser(uuid);
                userMap.put(uuid, user);
                future.complete(user);
            });
        }
        return future;
    }

    public User getUserSync(UUID uuid) {
        return userMap.computeIfAbsent(uuid, u -> implementation.loadUser(u));
    }

    public void saveUser(UUID uuid) {
        if (userMap.containsKey(uuid)) plugin.runAsync(() -> implementation.saveUser(userMap.get(uuid)));
    }

    private void saveAllSync() {
        for (User user : userMap.values()) implementation.saveUser(user);
    }

    public void shutdown() {
        HandlerList.unregisterAll(this);
        autosaveTask.cancel();
        saveAllSync();
        implementation.shutdown();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        saveUser(event.getPlayer().getUniqueId());
    }

    public class CacheAutosaveTask extends BukkitRunnable {
        @Override
        public void run() {
            saveAllSync();

            for (UUID uuid : new HashSet<>(userMap.keySet())) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline()) {
                    userMap.remove(uuid);
                }
            }
        }
    }
}
