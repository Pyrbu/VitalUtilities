package lol.pyr.utilities.staffnotifications;

import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record StaffNotificationToggleCommand(UtilitiesPlugin plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessages().get("only-player-command"));
            return true;
        }
        plugin.runAsync(() -> plugin.getStorage().getUser(player.getUniqueId()).thenAccept((user) -> {
            user.setStaffNotificationsEnabled(!user.isStaffNotificationsEnabled());
            if (user.isStaffNotificationsEnabled()) sender.sendMessage(plugin.getMessages().get(player, "notifications-enabled"));
            else sender.sendMessage(plugin.getMessages().get(player, "notifications-disabled"));
            plugin.getStorage().updateUser(user);
        }));
        return true;
    }

}
