package me.pyr.utilities.storage.implementations;

import me.pyr.utilities.UtilitiesPlugin;
import me.pyr.utilities.storage.UtilitiesStorageProvider;
import me.pyr.utilities.storage.UtilitiesUser;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class YamlStorage implements UtilitiesStorageProvider, Listener {

    private final UtilitiesPlugin plugin;
    private final Map<UUID, UtilitiesUser> cache = new HashMap<>();

    public YamlStorage(UtilitiesPlugin plugin) {
        this.plugin = plugin;
    }

    private UtilitiesUser loadUser(UUID uuid) {
        File file = new File(plugin.getDataFolder(), "users/" + uuid.toString() + ".yml");
        UtilitiesUser user = new UtilitiesUser(uuid);
        if (!file.exists()) return user;

        // TODO: Reminder to always add user values here

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        user.setCommandSpyEnabled(config.getBoolean("command-spy-enabled", false));
        return user;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveUser(UtilitiesUser user) {
        File file = new File(plugin.getDataFolder(), "users/" + user.getUuid().toString() + ".yml");

        if (!file.exists()) try {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException exception) {
            plugin.getLogger().severe("Could not create userdata file for " + user.getUuid());
            exception.printStackTrace();
        }

        // TODO: Reminder to always add user values here

        YamlConfiguration config = new YamlConfiguration();
        config.set("command-spy-enabled", user.isCommandSpyEnabled());

        if (!file.exists()) try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().severe("Could not save userdata for " + user.getUuid());
            exception.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<UtilitiesUser> getUser(UUID uuid) {
        CompletableFuture<UtilitiesUser> future = new CompletableFuture<>();
        if (cache.containsKey(uuid)) future.complete(cache.get(uuid));
        else plugin.runAsync(() -> {
            UtilitiesUser user = loadUser(uuid);
            cache.put(uuid, user);
            future.complete(user);
        });
        return future;
    }

    @Override
    public void updateUser(UtilitiesUser user) {
        if (!user.equals(cache.get(user.getUuid()))) cache.put(user.getUuid(), user);
    }

    @Override
    public void shutdown() {
        for (UtilitiesUser user : cache.values()) saveUser(user);
        cache.clear();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        plugin.runAsync(() -> cache.put(uuid, loadUser(uuid)));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final UtilitiesUser user = cache.get(event.getPlayer().getUniqueId());
        if (user == null) return;
        plugin.runAsync(() -> saveUser(user));
    }

}
