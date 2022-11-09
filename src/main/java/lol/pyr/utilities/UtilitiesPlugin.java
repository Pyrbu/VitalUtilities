package lol.pyr.utilities;

import lol.pyr.extendedcommands.CommandManager;
import lol.pyr.extendedcommands.MessageKey;
import lol.pyr.utilities.chat.BroadcastCommand;
import lol.pyr.utilities.chat.NetworkBroadcastCommand;
import lol.pyr.utilities.chat.commandspy.CommandSpyCommand;
import lol.pyr.utilities.chat.commandspy.CommandSpyListener;
import lol.pyr.utilities.chat.staff.*;
import lol.pyr.utilities.commands.gamemode.GamemodeCommand;
import lol.pyr.utilities.commands.gamemode.GamemodeShortcutCommand;
import lol.pyr.utilities.commands.player.*;
import lol.pyr.utilities.commands.teleport.TpallCommand;
import lol.pyr.utilities.commands.teleport.TphereCommand;
import lol.pyr.utilities.configuration.UtilitiesConfiguration;
import lol.pyr.utilities.configuration.UtilitiesMessages;
import lol.pyr.utilities.network.NetworkMessenger;
import lol.pyr.utilities.storage.StorageCacheLayer;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class UtilitiesPlugin extends JavaPlugin {
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

        utilitiesConfig.registerReloadHook(() -> {
            if (storage != null) storage.shutdown();
            storage = new StorageCacheLayer(this, utilitiesConfig.getStorageType());
            getLogger().info("Initialised Storage [" + utilitiesConfig.getStorageType().toString() + "]");
        });

        utilitiesConfig.registerReloadHook(() -> {
            if (networkConnection != null) networkConnection.shutdown();
            if (!utilitiesConfig.isEnableNetworkFeatures()) {
                networkConnection = null;
                getLogger().info("Skipping Network Features");
                return;
            }
            if (networkConnection == null) networkConnection = new NetworkMessenger(this);
            else networkConnection.connect();
            getLogger().info("Initialised Network Features");
        });

        utilitiesConfig.registerReloadHook(() -> getLogger().info("Configurations loaded"));
        utilitiesConfig.reload();

        registerListeners();
        getLogger().info("Registered listeners");

        registerCommands();
        getLogger().info("Registered commands");
    }

    @Override
    public void onDisable() {
        if (storage != null) storage.shutdown();
        if (networkConnection != null) networkConnection.shutdown();
        getLogger().info("Shutdown complete");
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new CommandSpyListener(this), this);
        pm.registerEvents(new StaffNotificationListener(this), this);
        pm.registerEvents(new StaffChatListener(this), this);
    }

    private void registerCommands() {
        CommandManager<UtilitiesPlugin> manager = new CommandManager<>(this);
        manager.registerDefaultParsers();
        manager.setMessageResolver(MessageKey.NOT_ENOUGH_ARGS, context -> messages.get("incorrect-usage", context.getCurrentUsage()));
        manager.setMessageResolver(MessageKey.SENDER_REQUIRED_PLAYER, context -> messages.get("player-only-command"));
        manager.setDefaultResolver(context -> messages.get("incorrect-usage", context.getCurrentUsage()));

        manager.registerCommand("vitalutilities", new UtilitiesCommand());
        manager.registerCommand("commandspy", new CommandSpyCommand());
        manager.registerCommand("gamemode", new GamemodeCommand());
        manager.registerCommand("gms", new GamemodeShortcutCommand(GameMode.SURVIVAL));
        manager.registerCommand("gmc", new GamemodeShortcutCommand(GameMode.CREATIVE));
        manager.registerCommand("gma", new GamemodeShortcutCommand(GameMode.ADVENTURE));
        manager.registerCommand("gmsp", new GamemodeShortcutCommand(GameMode.SPECTATOR));
        manager.registerCommand("clearinventory", new ClearInventoryCommand());
        manager.registerCommand("broadcast", new BroadcastCommand());
        manager.registerCommand("networkbroadcast", new NetworkBroadcastCommand());
        manager.registerCommand("togglestaffnotifications", new StaffNotificationToggleCommand());
        manager.registerCommand("heal", new HealCommand());
        manager.registerCommand("feed", new FeedCommand());
        manager.registerCommand("sudo", new SudoCommand());
        manager.registerCommand("fly", new FlyCommand());
        manager.registerCommand("togglestaffchat", new StaffChatToggleCommand());
        manager.registerCommand("staffchat", new StaffChatCommand());
        manager.registerCommand("tphere", new TphereCommand());
        manager.registerCommand("tpall", new TpallCommand());
    }

    public void runSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this, runnable);
    }

    public void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
    }
}
