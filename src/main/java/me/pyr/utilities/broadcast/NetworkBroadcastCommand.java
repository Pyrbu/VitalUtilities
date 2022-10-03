package me.pyr.utilities.broadcast;

import me.pyr.utilities.UtilitiesPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public record NetworkBroadcastCommand(UtilitiesPlugin plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getUtilitiesConfig().isEnableNetworkFeatures()) {
            sender.sendMessage(plugin.getMessages().get("no-network-features"));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessages().get("incorrect-usage", label + " <message>"));
            return true;
        }
        plugin.getNetworkConnection().broadcastMessage(String.join(" ", args));
        return true;
    }

}
