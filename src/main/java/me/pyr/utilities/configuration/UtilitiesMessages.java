package me.pyr.utilities.configuration;

import com.google.common.base.Charsets;
import me.clip.placeholderapi.PlaceholderAPI;
import me.pyr.utilities.UtilitiesPlugin;
import me.pyr.utilities.util.HexColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class UtilitiesMessages {

    private final String FILENAME = "messages.yml";
    private final UtilitiesPlugin plugin;
    private final Map<String, String> messages = new HashMap<>();
    private final File file;

    @SuppressWarnings("ConstantConditions")
    public UtilitiesMessages(UtilitiesPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), FILENAME);
        if (!file.exists()) try {
            Files.copy(plugin.getResource(FILENAME), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            plugin.getLogger().severe("Could not copy default messages.yml file!");
            exception.printStackTrace();
        }
        reload();
    }

    private void loadDefaults(YamlConfiguration config) {
        InputStream defaultConfigStream = plugin.getResource(FILENAME);
        if (defaultConfigStream != null) config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream, Charsets.UTF_8)));
    }

    public void reload() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        loadDefaults(config);
        messages.clear();
        String prefix = HexColorUtil.translateFully(config.getString("prefix"));
        for (String key : config.getKeys(false)) messages.put(key, HexColorUtil.translateFully(config.getString(key)).replace("{P}", prefix));
    }

    private String format(String id, String[] strings) {
        String msg = messages.get(id);
        for (int i = 0; i < strings.length; i++) msg = msg.replace("{" + i + "}", strings[i]);
        return msg;
    }

    public String get(String id, String... strings) {
        return get(null, id, strings);
    }

    public String get(Player player, String id, String... strings) {
        String msg = format(id, strings);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) msg = PlaceholderAPI.setPlaceholders(player, msg);
        return msg;
    }

    public PapiMessageRecycler recycler(String id, String... strings) {
        return new PapiMessageRecycler(format(id, strings));
    }

    public record PapiMessageRecycler(String message) {
        public String get(Player player) {
            if (player == null) return message;
            return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") ? PlaceholderAPI.setPlaceholders(player, message) : message;
        }
    }
}
