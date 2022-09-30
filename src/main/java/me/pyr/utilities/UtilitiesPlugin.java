package me.pyr.utilities;

import lombok.Getter;
import me.pyr.utilities.commandspy.CommandSpyCommand;
import me.pyr.utilities.commandspy.CommandSpyListener;
import me.pyr.utilities.configuration.UtilitiesMessages;
import me.pyr.utilities.configuration.UtilitiesConfiguration;
import me.pyr.utilities.gamemode.GamemodeCommand;
import me.pyr.utilities.gamemode.GamemodeShortcutCommand;
import me.pyr.utilities.storage.UtilitiesStorageProvider;
import me.pyr.utilities.storage.implementations.MongoDBStorage;
import me.pyr.utilities.storage.implementations.YamlStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class UtilitiesPlugin extends JavaPlugin {

    private boolean enabled = false;

    @Getter private UtilitiesConfiguration config;
    @Getter private UtilitiesMessages messages;

    @Getter private UtilitiesStorageProvider storage;

    @Override
    public void onEnable() {
        getLogger().info(" ");
        getLogger().info("Welcome to " + getDescription().getName());
        getLogger().info("Made with love by " + String.join(", ", getDescription().getAuthors()));
        getLogger().info(" ");

        config = new UtilitiesConfiguration(this);
        messages = new UtilitiesMessages(this);
        getLogger().info("Configurations loaded");

        initialiseStorage();
        getLogger().info("Initialised Storage [" + config.getStorageType().toString() + "]");

        registerListeners();
        getLogger().info("Registered listeners");

        registerCommands();
        getLogger().info("Registered commands");

        enabled = true;
        getLogger().info("Plugin ready");
    }

    @Override
    public void onDisable() {
        if (!enabled) return;
        storage.shutdown();
        getLogger().info("Shutdown complete");
    }

    private void initialiseStorage() {
        switch (config.getStorageType()) {
            case YAML -> storage = new YamlStorage(this);
            case MONGODB -> storage = new MongoDBStorage(this);
        }
        if (storage instanceof Listener listener) Bukkit.getPluginManager().registerEvents(listener, this);
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new CommandSpyListener(this), this);
    }

    @SuppressWarnings("ConstantConditions")
    private void registerCommands() {
        getCommand("vitalutilities").setExecutor(this);
        getCommand("commandspy").setExecutor(new CommandSpyCommand(this));
        getCommand("gamemode").setExecutor(new GamemodeCommand());
        getCommand("gms").setExecutor(new GamemodeShortcutCommand(GameMode.SURVIVAL));
        getCommand("gmc").setExecutor(new GamemodeShortcutCommand(GameMode.CREATIVE));
        getCommand("gma").setExecutor(new GamemodeShortcutCommand(GameMode.ADVENTURE));
        getCommand("gmsp").setExecutor(new GamemodeShortcutCommand(GameMode.SPECTATOR));
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
            if (storage instanceof Listener listener) HandlerList.unregisterAll(listener);
            storage.shutdown();
            config.reload();
            messages.reload();
            initialiseStorage();
            sender.sendMessage(ChatColor.GREEN + "All configurations & storage have been reloaded (" + (System.currentTimeMillis() - before) + "ms)");
        }

        else {
            sender.sendMessage(ChatColor.GREEN + "Available subcommands:\n" +
                    " /" + label + " reload - Reloads all configurations & storage");
        }

        return true;
    }
}
