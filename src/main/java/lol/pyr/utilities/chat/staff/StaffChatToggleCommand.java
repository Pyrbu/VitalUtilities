package lol.pyr.utilities.chat.staff;

import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record StaffChatToggleCommand(UtilitiesPlugin plugin) implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessages().get("only-player-command"));
            return true;
        }
        plugin.runAsync(() -> plugin.getStorage().getUser(player.getUniqueId()).thenAccept((user) -> {
            user.setStaffChatToggled(!user.isStaffChatToggled());
            if (user.isStaffChatToggled()) sender.sendMessage(plugin.getMessages().get(player, "staffchat-enabled"));
            else sender.sendMessage(plugin.getMessages().get(player, "staffchat-disabled"));
        }));
        return true;
    }
}
