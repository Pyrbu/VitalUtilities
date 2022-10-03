package me.pyr.utilities.network;

import me.clip.placeholderapi.PlaceholderAPI;
import me.pyr.utilities.UtilitiesPlugin;
import me.pyr.utilities.util.HexColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;

public class NetworkConnection {

    private final static String CHANNEL_NAME = "vitalutilities";

    private final UtilitiesPlugin plugin;
    private Jedis jedis;

    public NetworkConnection(UtilitiesPlugin plugin) {
        this.plugin = plugin;
        connect();
    }

    public void connect() {
        try {
            if (jedis != null) jedis.disconnect();
            jedis = new Jedis(
                    plugin.getUtilitiesConfig().getRedisHostname(),
                    plugin.getUtilitiesConfig().getRedisPort(),
                    DefaultJedisClientConfig.builder()
                            .user(plugin.getUtilitiesConfig().getRedisUsername())
                            .password(plugin.getUtilitiesConfig().getRedisPassword())
                            .build());
            jedis.subscribe(new NetworkSubscriber(), CHANNEL_NAME);
        } catch (Exception exception) {
            plugin.getLogger().severe("Redis encountered an error while connecting");
            exception.printStackTrace();
        }
    }

    public void shutdown() {
        if (jedis != null) jedis.disconnect();
    }

    public void broadcastMessage(String message) {
        internalBroadcast(message);
        jedis.publish(CHANNEL_NAME, "broadcast;" + message);
    }

    public void broadcastWithPermission(String permission, String message) {
        internalBroadcastWithPermission(permission, message);
        jedis.publish(CHANNEL_NAME, "permissionbroadcast;" + permission + ";" + message);
    }

    protected static String papi(Player player, String message) {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") ? PlaceholderAPI.setPlaceholders(player, message) : message;
    }

    protected static void internalBroadcast(String message) {
        message = HexColorUtil.translateFully(message);
        for (Player player : Bukkit.getOnlinePlayers()) player.sendMessage(papi(player, message));
    }

    protected static void internalBroadcastWithPermission(String permission, String message) {
        message = HexColorUtil.translateFully(message);
        for (Player player : Bukkit.getOnlinePlayers()) if (player.hasPermission(permission)) player.sendMessage(papi(player, message));
    }

}
