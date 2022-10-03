package me.pyr.utilities.network;

import me.pyr.utilities.UtilitiesPlugin;
import redis.clients.jedis.Jedis;

public class NetworkConnection {

    private final static String CHANNEL_NAME = "vitalutilities";

    private final UtilitiesPlugin plugin;
    private Jedis subscriber;
    private Jedis publisher;
    private NetworkSubscriber networkSubscriber;

    public NetworkConnection(UtilitiesPlugin plugin) {
        this.plugin = plugin;
        connect();
    }

    public void connect() {
        try {
            subscriber = new Jedis(
                    plugin.getUtilitiesConfig().getRedisHostname(),
                    plugin.getUtilitiesConfig().getRedisPort());
            subscriber.auth(plugin.getUtilitiesConfig().getRedisPassword());
            networkSubscriber = new NetworkSubscriber();
            plugin.runAsync(() -> subscriber.subscribe(networkSubscriber, CHANNEL_NAME));

            publisher = new Jedis(
                    plugin.getUtilitiesConfig().getRedisHostname(),
                    plugin.getUtilitiesConfig().getRedisPort());
            publisher.auth(plugin.getUtilitiesConfig().getRedisPassword());
        } catch (Exception exception) {
            plugin.getLogger().severe("Redis encountered an error while connecting");
            exception.printStackTrace();
        }
    }

    public void shutdown() {
        if (networkSubscriber != null) networkSubscriber.unsubscribe(CHANNEL_NAME);
        if (subscriber != null) subscriber.disconnect();
        if (publisher != null) publisher.disconnect();
    }

    public void broadcastMessage(String message) {
        plugin.runAsync(() -> publisher.publish(CHANNEL_NAME, "broadcast;" + message));
    }

    public void broadcastWithPermission(String permission, String message) {
        plugin.runAsync(() -> publisher.publish(CHANNEL_NAME, "permissionbroadcast;" + permission + ";" + message));
    }

}
