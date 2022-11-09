package lol.pyr.utilities.configuration;

import com.google.common.base.Charsets;
import lol.pyr.utilities.UtilitiesPlugin;
import lol.pyr.utilities.storage.StorageType;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class UtilitiesConfiguration {
    private final String FILENAME = "config.yml";
    private final UtilitiesPlugin plugin;
    private final File file;
    private final List<Runnable> hooks = new ArrayList<>();

    @Getter private StorageType storageType;
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

        redisHostname = getString(config, "redis.hostname");
        redisPort = getInt(config, "redis.port");
        redisUsername = getString(config, "redis.username");
        redisPassword = getString(config, "redis.password");

        enableNetworkFeatures = getBoolean(config, "enable-network-features");
        networkServerName = getString(config, "server-name");

        mongoHostname = getString(config, "mongo.hostname");
        mongoPort = getInt(config, "mongo.port");
        mongoUsername = getString(config, "mongo.username");
        mongoPassword = getString(config, "mongo.password");
        mongoDatabase = getString(config, "mongo.database");

        for (Runnable hook : hooks) hook.run();
    }

    public void registerReloadHook(Runnable runnable) {
        hooks.add(runnable);
    }

    private void warn(String key) {
        plugin.getLogger().warning("Missing configuration key " + key + "! Add it to the config or reset your config & it will appear. Using the default value.");
    }

    private String getString(YamlConfiguration config, String key) {
        if (!config.contains(key, true)) warn(key);
        return config.getString(key);
    }

    private int getInt(YamlConfiguration config, String key) {
        if (!config.contains(key, true)) warn(key);
        return config.getInt(key);
    }

    private boolean getBoolean(YamlConfiguration config, String key) {
        if (!config.contains(key, true)) warn(key);
        return config.getBoolean(key);
    }
}
