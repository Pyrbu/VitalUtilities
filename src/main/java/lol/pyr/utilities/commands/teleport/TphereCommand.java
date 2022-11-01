package lol.pyr.utilities.commands.teleport;

import lol.pyr.utilities.UtilitiesPlugin;
import lol.pyr.utilities.util.CompletionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public record TphereCommand(UtilitiesPlugin plugin) implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessages().get("only-player-command"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(plugin.getMessages().get(player, "incorrect-usage", label + " <player>"));
            return true;
        }

        Player p1 = Bukkit.getPlayer(args[0]);
        if (p1 == null) {
            player.sendMessage(plugin.getMessages().get(player, "player-not-online", args[0]));
            return true;
        }
        p1.teleport(player.getLocation());
        p1.sendMessage(plugin.getMessages().get(p1, "teleported-self", player.getName()));
        player.sendMessage(plugin.getMessages().get(player, "teleported-to-self", p1.getName()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return CompletionUtil.players(args[0]);
        return new ArrayList<>();
    }
}
