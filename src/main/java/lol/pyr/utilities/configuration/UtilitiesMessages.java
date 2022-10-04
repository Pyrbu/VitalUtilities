package lol.pyr.utilities.configuration;

import com.google.common.base.Charsets;
import lol.pyr.utilities.UtilitiesPlugin;
import lol.pyr.utilities.util.HexColorUtil;
import me.clip.placeholderapi.PlaceholderAPI;
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

    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    public UtilitiesMessages(UtilitiesPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), FILENAME);
        if (!file.exists()) try {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            Files.copy(plugin.getResource(FILENAME), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            plugin.getLogger().severe("Could not copy default messages.yml file!");
            exception.printStackTrace();
        }
        reload();
    }

    private void loadDefaults() {
        InputStream defaultConfigStream = plugin.getResource(FILENAME);
        if (defaultConfigStream != null) {
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream, Charsets.UTF_8));
            String prefix = HexColorUtil.translateFully(defaults.getString("prefix"));
            for (String key : defaults.getKeys(false)) messages.put(key, HexColorUtil.translateFully(defaults.getString(key)).replace("{P}", prefix));
        }
    }

    public void reload() {
        messages.clear();
        loadDefaults();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
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
