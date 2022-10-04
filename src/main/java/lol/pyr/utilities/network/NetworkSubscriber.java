package lol.pyr.utilities.network;

import lol.pyr.utilities.UtilitiesPlugin;
import lol.pyr.utilities.util.HexColorUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.Arrays;

public class NetworkSubscriber extends JedisPubSub implements Runnable {

    private final UtilitiesPlugin plugin;
    private final JedisPool jedisPool;
    private final NetworkMessenger messenger;

    public NetworkSubscriber(UtilitiesPlugin plugin, NetworkMessenger messenger, JedisPool jedisPool) {
        this.plugin = plugin;
        this.jedisPool = jedisPool;
        this.messenger = messenger;
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        boolean first = true;
        while (!messenger.isClosing() && !Thread.interrupted() && !jedisPool.isClosed()) {
            try (Jedis jedis = jedisPool.getResource()) {
                if (first) {
                    first = false;
                } else {
                    plugin.getLogger().info("Redis pubsub connection re-established");
                }

                jedis.subscribe(this, NetworkMessenger.CHANNEL_NAME); // blocking call
            } catch (Exception e) {
                if (messenger.isClosing()) {
                    return;
                }

                plugin.getLogger().warning("Redis pubsub connection dropped, trying to re-open the connection");
                try {
                    unsubscribe();
                } catch (Exception ignored) {}

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void onMessage(String channel, String message) {
        String[] split = message.split(";");

        if (split[0].equalsIgnoreCase("broadcast")) {
            String msg = HexColorUtil.translateFully(String.join(";", Arrays.copyOfRange(split, 1, split.length)));
            Bukkit.getServer().getLogger().info("[Network] " + msg);
            for (Player player : Bukkit.getOnlinePlayers()) player.sendMessage(papi(player, msg));
        }

        else if (split[0].equalsIgnoreCase("broadcastpermission")) {
            String msg = HexColorUtil.translateFully(String.join(";", Arrays.copyOfRange(split, 2, split.length)));
            for (Player player : Bukkit.getOnlinePlayers()) if (player.hasPermission(split[1])) player.sendMessage(papi(player, msg));
        }
    }

    private static String papi(Player player, String message) {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") ? PlaceholderAPI.setPlaceholders(player, message) : message;
    }

}
