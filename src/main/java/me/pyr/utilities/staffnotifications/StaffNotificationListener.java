package me.pyr.utilities.staffnotifications;

import me.pyr.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public record StaffNotificationListener(UtilitiesPlugin plugin) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission("utilities.staffnotifications")) return;
        sendMessage(plugin.getMessages().get(event.getPlayer(),"notifications-staff-join", event.getPlayer().getName(), plugin.getUtilitiesConfig().getNetworkServerName()));
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        if (!event.getPlayer().hasPermission("utilities.staffnotifications")) return;
        sendMessage(plugin.getMessages().get(event.getPlayer(),"notifications-staff-join", event.getPlayer().getName(), event.getNewGameMode().toString()));
    }

    private void sendMessage(String message) {
        if (plugin.getUtilitiesConfig().isEnableNetworkFeatures()) plugin.getNetworkConnection().broadcastWithPermission("utilities.staffnotifications", message);
        else for (Player player : Bukkit.getOnlinePlayers()) if (player.hasPermission("utilities.staffnotifications")) player.sendMessage(message);
    }
}
