package lol.pyr.utilities.network;

import lol.pyr.utilities.UtilitiesPlugin;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class NetworkMessenger {

    protected final static String CHANNEL_NAME = "vitalutilities";

    private final UtilitiesPlugin plugin;
    private JedisPool jedisPool;
    private NetworkSubscriber subscriber;

    @Getter private boolean closing;

    public NetworkMessenger(UtilitiesPlugin plugin) {
        this.plugin = plugin;
        connect();
    }

    public void connect() {
        jedisPool = new JedisPool(new JedisPoolConfig(),
                plugin.getUtilitiesConfig().getRedisHostname(),
                plugin.getUtilitiesConfig().getRedisPort(),
                Protocol.DEFAULT_TIMEOUT,
                plugin.getUtilitiesConfig().getRedisUsername(),
                plugin.getUtilitiesConfig().getRedisPassword(), false);

        subscriber = new NetworkSubscriber(plugin, this, jedisPool);
        plugin.runAsync(subscriber);
    }

    public void shutdown() {
        closing = true;
        subscriber.unsubscribe();
        jedisPool.destroy();
    }

    public void broadcastMessage(String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            plugin.runAsync(() -> jedis.publish(CHANNEL_NAME, "broadcast;" + message));
        }
    }

    public void broadcastWithPermission(String permission, String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            plugin.runAsync(() -> jedis.publish(CHANNEL_NAME, "permissionbroadcast;" + permission + ";" + message));
        }
    }

}
