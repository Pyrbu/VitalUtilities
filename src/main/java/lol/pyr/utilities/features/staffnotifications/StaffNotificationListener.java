package lol.pyr.utilities.features.staffnotifications;

import lol.pyr.utilities.UtilitiesPlugin;
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
        sendMessage(event.getPlayer(), plugin.getMessages().get(event.getPlayer(),"notifications-staff-join", event.getPlayer().getName(), plugin.getUtilitiesConfig().getNetworkServerName()));
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        if (!event.getPlayer().hasPermission("utilities.staffnotifications")) return;
        sendMessage(event.getPlayer(), plugin.getMessages().get(event.getPlayer(),"notifications-staff-gamemode", event.getPlayer().getName(), event.getNewGameMode().toString()));
    }

    private void sendMessage(Player player, String message) {
        if (plugin.getUtilitiesConfig().isEnableNetworkFeatures()) plugin.getNetworkConnection().staffNotification(player.getName(), message);
        else for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("utilities.staffnotifications")) continue;
            plugin.getStorage().getUser(p.getUniqueId()).thenAcceptAsync((user) -> {
                if (user.isStaffNotificationsEnabled()) p.sendMessage(message);
            });
        }
    }
}
