package me.pyr.utilities.network;

import redis.clients.jedis.JedisPubSub;

import java.util.Arrays;

public class NetworkSubscriber extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        String[] split = message.split(";");

        if (split[0].equalsIgnoreCase("broadcast")) {
            NetworkConnection.internalBroadcast(String.join(";", Arrays.copyOfRange(split, 1, split.length)));
        }

        else if (split[0].equalsIgnoreCase("broadcastpermission")) {
            NetworkConnection.internalBroadcastWithPermission(split[1], String.join(";", Arrays.copyOfRange(split, 2, split.length)));
        }
    }

}
