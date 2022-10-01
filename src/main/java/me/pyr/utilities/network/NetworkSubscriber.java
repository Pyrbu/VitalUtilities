package me.pyr.utilities.network;

import me.pyr.utilities.util.HexColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;

import java.util.Arrays;

public class NetworkSubscriber extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        String[] split = message.split(";");

        if (split[0].equalsIgnoreCase("broadcast")) {
            Bukkit.broadcastMessage(HexColorUtil.translateFully(String.join(";", Arrays.copyOfRange(split, 1, split.length))));
        }

        else if (split[0].equalsIgnoreCase("broadcastpermission")) {
            String msg = HexColorUtil.translateFully(String.join(";", Arrays.copyOfRange(split, 1, split.length)));
            for (Player player : Bukkit.getOnlinePlayers()) player.sendMessage(msg);
        }
    }

}
