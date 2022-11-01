package lol.pyr.utilities.chat.staff;

import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record StaffChatCommand(UtilitiesPlugin plugin) implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessages().get("incorrect-usage", label + " <message>"));
            return true;
        }
        sendMessage(plugin.getMessages().get("staff-chat", sender.getName(), String.join(" ", args)));
        return true;
    }

    private void sendMessage(String message) {
        if (plugin.getUtilitiesConfig().isEnableNetworkFeatures()) plugin.getNetworkConnection().broadcastWithPermission("utilities.staffchat", message);
        else for (Player p : Bukkit.getOnlinePlayers()) if (p.hasPermission("utilities.staffchat")) p.sendMessage(message);
    }
}
