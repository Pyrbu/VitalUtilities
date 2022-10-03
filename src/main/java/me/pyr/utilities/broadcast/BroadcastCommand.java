package me.pyr.utilities.broadcast;

import me.pyr.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record BroadcastCommand(UtilitiesPlugin plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessages().get("incorrect-usage", label + " <message>"));
            return true;
        }
        for (Player player : Bukkit.getOnlinePlayers()) player.sendMessage(plugin.getMessages().get(player, "broadcast", String.join(" ", args)));
        return true;
    }

}
