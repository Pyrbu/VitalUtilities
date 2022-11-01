package lol.pyr.utilities.chat.commandspy;

import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record CommandSpyCommand(UtilitiesPlugin plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessages().get("only-player-command"));
            return true;
        }
        plugin.runAsync(() -> plugin.getStorage().getUser(player.getUniqueId()).thenAccept((user) -> {
            user.setCommandSpyEnabled(!user.isCommandSpyEnabled());
            if (user.isCommandSpyEnabled()) sender.sendMessage(plugin.getMessages().get(player, "command-spy-enabled"));
            else sender.sendMessage(plugin.getMessages().get(player, "command-spy-disabled"));
        }));
        return true;
    }

}
