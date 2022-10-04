package lol.pyr.utilities.storage;

import lol.pyr.utilities.UtilitiesPlugin;
import lol.pyr.utilities.storage.implementations.StorageType;
import lol.pyr.utilities.storage.implementations.mongodb.MongoDBStorage;
import lol.pyr.utilities.storage.implementations.yaml.YamlStorage;
import lol.pyr.utilities.storage.model.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("BusyWait")
public class StorageCacheLayer implements Listener {

    private final UtilitiesPlugin plugin;
    private StorageImplementationProvider implementation;
    private final HashMap<UUID, User> userMap = new HashMap<>();
    private boolean lock = false;

    public StorageCacheLayer(UtilitiesPlugin plugin, StorageType type) {
        this.plugin = plugin;
        switch (type) {
            case YAML -> implementation = new YamlStorage(plugin);
            case MONGODB -> implementation = new MongoDBStorage(plugin);
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public CompletableFuture<User> getUser(UUID uuid) {
        CompletableFuture<User> future = new CompletableFuture<>();
        if (userMap.containsKey(uuid)) future.complete(userMap.get(uuid));
        else {
            plugin.runAsync(() -> {
                while (lock) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {}
                }

                User user = implementation.loadUser(uuid);
                lock = true;
                userMap.put(uuid, user);
                lock = false;
                future.complete(user);
            });
        }
        return future;
    }

    public void saveUser(UUID uuid) {
        if (userMap.containsKey(uuid)) plugin.runAsync(() -> implementation.saveUser(userMap.get(uuid)));
    }

    public void saveAll() {
        for (UUID uuid : new HashSet<>(userMap.keySet())) saveUser(uuid);
    }

    public void shutdown() {
        lock = true;
        HandlerList.unregisterAll(this);
        saveAll();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        saveUser(event.getPlayer().getUniqueId());
    }

    public class CacheAutosaveTask extends BukkitRunnable {
        @Override
        public void run() {
            saveAll();
            for (UUID uuid : new HashSet<>(userMap.keySet())) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline()) {
                    userMap.remove(uuid);
                }
            }
        }
    }

}
