package me.pyr.utilities.network;

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
        Bukkit.broadcastMessage(HexColorUtil.translateFully(message));
        jedis.publish(CHANNEL_NAME, "broadcast;" + message);
    }

    public void broadcastWithPermission(String permission, String message) {
        String msg = HexColorUtil.translateFully(message);
        for (Player p : Bukkit.getOnlinePlayers()) p.sendMessage(msg);
        jedis.publish(CHANNEL_NAME, "permissionbroadcast;" + message);
    }

}
