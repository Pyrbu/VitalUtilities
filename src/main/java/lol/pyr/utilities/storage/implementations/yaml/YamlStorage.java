package lol.pyr.utilities.storage.implementations.yaml;

import lol.pyr.utilities.UtilitiesPlugin;
import lol.pyr.utilities.storage.StorageImplementationProvider;
import lol.pyr.utilities.storage.model.User;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class YamlStorage implements StorageImplementationProvider, Listener {

    private final UtilitiesPlugin plugin;
    private final Map<UUID, User> cache = new HashMap<>();

    public YamlStorage(UtilitiesPlugin plugin) {
        this.plugin = plugin;
    }

    public User loadUser(UUID uuid) {
        File file = new File(plugin.getDataFolder(), "users/" + uuid.toString() + ".yml");
        User user = new User(uuid);
        if (!file.exists()) return user;

        // TODO: Reminder to always add user values here

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        user.setCommandSpyEnabled(config.getBoolean("command-spy-enabled", false));
        user.setStaffChatToggled(config.getBoolean("staff-chat-toggled"));
        user.setStaffNotificationsEnabled(config.getBoolean("staff-notifications-enabled", true));
        return user;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveUser(User user) {
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
        config.set("staff-chat-toggled", user.isStaffChatToggled());
        config.set("staff-notifications-enabled", user.isStaffNotificationsEnabled());

        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().severe("Could not save userdata for " + user.getUuid());
            exception.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        for (User user : cache.values()) saveUser(user);
        cache.clear();
    }

}
