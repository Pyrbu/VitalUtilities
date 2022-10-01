package me.pyr.utilities.configuration;

import com.google.common.base.Charsets;
import lombok.Getter;
import me.pyr.utilities.UtilitiesPlugin;
import me.pyr.utilities.storage.UtilitiesStorageType;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class UtilitiesConfiguration {

    private final String FILENAME = "config.yml";
    private final UtilitiesPlugin plugin;
    private final File file;

    @Getter private UtilitiesStorageType storageType;
    @Getter private final HashMap<GameMode, Set<String>> gameModeAliases = new HashMap<>();

    @SuppressWarnings("ConstantConditions")
    public UtilitiesConfiguration(UtilitiesPlugin plugin) {
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
        if (defaultConfigStream != null) {
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream, Charsets.UTF_8)));
        }
    }

    public void reload() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        loadDefaults(config);

        try {
            storageType = UtilitiesStorageType.valueOf(config.getString("storage-type"));
        } catch (IllegalArgumentException ex) {
            storageType = UtilitiesStorageType.YAML;
            plugin.getLogger().severe("Invalid storage type (" + config.getString("storage-type") + ") Defaulting to " + storageType.toString());
        }

        gameModeAliases.clear();
        for (GameMode gamemode : GameMode.values()) {
            Set<String> set = config.getStringList("gamemode-aliases." + gamemode.toString().toLowerCase()).stream()
                    .map(String::toLowerCase).collect(Collectors.toSet());
            set.add(gamemode.toString().toLowerCase());
            gameModeAliases.put(gamemode, set);
        }
    }
}
