package me.pyr.utilities.network;

import me.clip.placeholderapi.PlaceholderAPI;
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
