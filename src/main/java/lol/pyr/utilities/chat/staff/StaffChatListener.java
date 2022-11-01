package lol.pyr.utilities.chat.staff;

import lol.pyr.utilities.UtilitiesPlugin;
import lol.pyr.utilities.storage.model.User;
import lol.pyr.utilities.util.HexColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public record StaffChatListener(UtilitiesPlugin plugin) implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().hasPermission("utilities.staffchat")) return;
        User user = plugin.getStorage().getUserSync(event.getPlayer().getUniqueId());
        if (!user.isStaffChatToggled()) return;
        event.setCancelled(true);
        sendMessage(plugin.getMessages().get("staff-chat", event.getPlayer().getName(), HexColorUtil.translateFully(event.getMessage())));
    }

    private void sendMessage(String message) {
        if (plugin.getUtilitiesConfig().isEnableNetworkFeatures()) plugin.getNetworkConnection().broadcastWithPermission("utilities.staffchat", message);
        else for (Player p : Bukkit.getOnlinePlayers()) if (p.hasPermission("utilities.staffchat")) p.sendMessage(message);
    }

}
