package me.pyr.utilities.commandspy;

import lombok.val;
import me.pyr.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public record CommandSpyListener(UtilitiesPlugin plugin) implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        plugin.runAsync(() -> {
            val recycler = plugin.getMessages().recycler("command-spy-format",
                    event.getPlayer().getName(),
                    event.getPlayer().getDisplayName(),
                    event.getMessage());
            for(Player player : Bukkit.getOnlinePlayers()) {
                plugin.getStorage().getUser(player.getUniqueId()).thenAccept((user) -> {
                    if (player.hasPermission("utilities.commandspy") && user.isCommandSpyEnabled())
                        player.sendMessage(recycler.get(player));
                });
            }
        });
    }
}
