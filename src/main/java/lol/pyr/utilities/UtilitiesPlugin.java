package lol.pyr.utilities;

import lol.pyr.utilities.features.misccommands.*;
import lol.pyr.utilities.features.commandspy.CommandSpyCommand;
import lol.pyr.utilities.features.commandspy.CommandSpyListener;
import lol.pyr.utilities.configuration.UtilitiesConfiguration;
import lol.pyr.utilities.configuration.UtilitiesMessages;
import lol.pyr.utilities.network.NetworkMessenger;
import lol.pyr.utilities.features.staffnotifications.StaffNotificationListener;
import lol.pyr.utilities.features.staffnotifications.StaffNotificationToggleCommand;
import lol.pyr.utilities.storage.StorageCacheLayer;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class UtilitiesPlugin extends JavaPlugin {

    private boolean enabled = false;

    @Getter private UtilitiesConfiguration utilitiesConfig;
    @Getter private UtilitiesMessages messages;
    @Getter private NetworkMessenger networkConnection;

    @Getter private StorageCacheLayer storage;

    @Override
    public void onEnable() {
        getServer().getLogger().info("");
        getServer().getLogger().info(ChatColor.GOLD + " \\  / |  |   " + ChatColor.YELLOW + getDescription().getName() + " " + ChatColor.GOLD + "v" + getDescription().getVersion());
        getServer().getLogger().info(ChatColor.GOLD + "  \\/  |__|   " + ChatColor.GRAY + "Made with " + ChatColor.RED + "\u2764 " + ChatColor.GRAY + " by " + String.join(", ", getDescription().getAuthors()));
        getServer().getLogger().info("");

        // BStats Metrics
        new Metrics(this, 16568);

        utilitiesConfig = new UtilitiesConfiguration(this);
        messages = new UtilitiesMessages(this);
        utilitiesConfig.registerReloadHook(() -> getLogger().info("Reloaded configuration"));
        getLogger().info("Configurations loaded");

        initialiseStorage();
        utilitiesConfig.registerReloadHook(() -> {
            storage.shutdown();
            initialiseStorage();
            getLogger().info("Reloaded storage");
        });
        getLogger().info("Initialised Storage [" + utilitiesConfig.getStorageType().toString() + "]");

        if (utilitiesConfig.isEnableNetworkFeatures()) {
            networkConnection = new NetworkMessenger(this);
            getLogger().info("Initialised Network Features");
        }
        utilitiesConfig.registerReloadHook(() -> {
            if (networkConnection != null) {
                networkConnection.shutdown();
                if (!utilitiesConfig.isEnableNetworkFeatures()) {
                    networkConnection = null;
                    return;
                }
            }
            if (networkConnection == null) networkConnection = new NetworkMessenger(this);
            else networkConnection.connect();
            getLogger().info("Reloaded network features");
        });

        registerListeners();
        getLogger().info("Registered listeners");

        registerCommands();
        getLogger().info("Registered commands");

        enabled = true;
    }

    @Override
    public void onDisable() {
        if (!enabled) return;
        storage.shutdown();
        if (networkConnection != null) networkConnection.shutdown();
        getLogger().info("Shutdown complete");
    }

    private void initialiseStorage() {
        storage = new StorageCacheLayer(this, utilitiesConfig.getStorageType());
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new CommandSpyListener(this), this);
        pm.registerEvents(new StaffNotificationListener(this), this);
    }

    @SuppressWarnings("ConstantConditions")
    private void registerCommands() {
        getCommand("vitalutilities").setExecutor(this);
        getCommand("commandspy").setExecutor(new CommandSpyCommand(this));
        getCommand("gamemode").setExecutor(new GamemodeCommand(this));
        getCommand("gms").setExecutor(new GamemodeShortcutCommand(this, GameMode.SURVIVAL));
        getCommand("gmc").setExecutor(new GamemodeShortcutCommand(this, GameMode.CREATIVE));
        getCommand("gma").setExecutor(new GamemodeShortcutCommand(this, GameMode.ADVENTURE));
        getCommand("gmsp").setExecutor(new GamemodeShortcutCommand(this, GameMode.SPECTATOR));
        getCommand("clearinventory").setExecutor(new ClearInventoryCommand(this));
        getCommand("broadcast").setExecutor(new BroadcastCommand(this));
        getCommand("networkbroadcast").setExecutor(new NetworkBroadcastCommand(this));
        getCommand("togglestaffnotifications").setExecutor(new StaffNotificationToggleCommand(this));
        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("feed").setExecutor(new FeedCommand(this));
        getCommand("tpall").setExecutor(new TpallCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("sudo").setExecutor(new SudoCommand(this));
    }

    public void runSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this, runnable);
    }

    public void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(messages.get("incorrect-usage", label + " help"));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            long before = System.currentTimeMillis();
            utilitiesConfig.reload();
            messages.reload();
            sender.sendMessage(ChatColor.GREEN + "All configurations & storage have been reloaded (" + (System.currentTimeMillis() - before) + "ms)");
        }

        else {
            sender.sendMessage(ChatColor.GREEN + "Available subcommands:\n" +
                    " /" + label + " reload - Reloads all configurations & storage");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Stream.of("reload", "help")
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

}
