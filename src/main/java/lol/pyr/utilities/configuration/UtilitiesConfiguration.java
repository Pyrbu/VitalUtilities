package lol.pyr.utilities.configuration;

import com.google.common.base.Charsets;
import lol.pyr.utilities.UtilitiesPlugin;
import lol.pyr.utilities.storage.StorageType;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UtilitiesConfiguration {

    private final String FILENAME = "config.yml";
    private final UtilitiesPlugin plugin;
    private final File file;
    private final List<Runnable> hooks = new ArrayList<>();

    @Getter private StorageType storageType;
    @Getter private final HashMap<GameMode, Set<String>> gameModeAliases = new HashMap<>();

    @Getter private String redisHostname;
    @Getter private int redisPort;
    @Getter private String redisUsername;
    @Getter private String redisPassword;

    @Getter private boolean enableNetworkFeatures;
    @Getter private String networkServerName;

    @Getter private String mongoHostname;
    @Getter private int mongoPort;
    @Getter private String mongoUsername;
    @Getter private String mongoPassword;
    @Getter private String mongoDatabase;

    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    public UtilitiesConfiguration(UtilitiesPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), FILENAME);
        if (!file.exists()) try {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            Files.copy(plugin.getResource(FILENAME), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            plugin.getLogger().severe("Could not copy default messages.yml file!");
            exception.printStackTrace();
        }
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
            storageType = StorageType.valueOf(config.getString("storage-type"));
        } catch (IllegalArgumentException ex) {
            storageType = StorageType.YAML;
            plugin.getLogger().severe("Invalid storage type (" + config.getString("storage-type") + ") Defaulting to " + storageType.toString());
        }

        gameModeAliases.clear();
        for (GameMode gamemode : GameMode.values()) {
            Set<String> set = config.getStringList("gamemode-aliases." + gamemode.toString().toLowerCase()).stream()
                    .map(String::toLowerCase).collect(Collectors.toSet());
            set.add(gamemode.toString().toLowerCase());
            gameModeAliases.put(gamemode, set);
        }

        redisHostname = config.getString("redis.hostname");
        redisPort = config.getInt("redis.port");
        redisUsername = config.getString("redis.username");
        redisPassword = config.getString("redis.password");

        enableNetworkFeatures = config.getBoolean("enable-network-features");
        networkServerName = config.getString("server-name");

        mongoHostname = config.getString("mongo.hostname");
        mongoPort = config.getInt("mongo.port");
        mongoUsername = config.getString("mongo.username");
        mongoPassword = config.getString("mongo.password");
        mongoDatabase = config.getString("mongo.database");

        for (Runnable hook : hooks) hook.run();
    }

    public void registerReloadHook(Runnable runnable) {
        hooks.add(runnable);
    }

}
