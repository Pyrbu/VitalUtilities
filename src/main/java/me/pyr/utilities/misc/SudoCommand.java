package me.pyr.utilities.misc;

import me.pyr.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public record SudoCommand(UtilitiesPlugin plugin) implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.getMessages().get("incorrect-usage", label + " <player> <command>"));
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(plugin.getMessages().get("player-not-online", args[0]));
            return true;
        }

        String cmd = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (cmd.toLowerCase().startsWith("c:")) {
            player.chat(cmd);
            sender.sendMessage(plugin.getMessages().get("sudo-chat", player.getName(), cmd));
        } else {
            player.performCommand(cmd);
            sender.sendMessage(plugin.getMessages().get("sudo-command", player.getName(), cmd));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }

}
