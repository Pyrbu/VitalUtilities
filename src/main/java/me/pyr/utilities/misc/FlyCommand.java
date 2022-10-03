package me.pyr.utilities.misc;

import me.pyr.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record FlyCommand(UtilitiesPlugin plugin) implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender.hasPermission("utilities.fly.others") && args.length > 0) {
            player = Bukkit.getPlayer(args[0]);
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessages().get("only-player-command"));
            return true;
        } else {
            player = (Player) sender;
        }

        if (player == null) {
            sender.sendMessage(plugin.getMessages().get("player-not-online", args[0]));
            return true;
        }

        player.setAllowFlight(!player.getAllowFlight());
        if (player.getAllowFlight()) {
            player.sendMessage(plugin.getMessages().get(player, "own-flying-enable"));
            if (player != sender) sender.sendMessage(plugin.getMessages().get("other-flying-enable", player.getName()));
        } else {
            player.sendMessage(plugin.getMessages().get(player, "own-flying-disable"));
            if (player != sender) sender.sendMessage(plugin.getMessages().get("other-flying-disable", player.getName()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender.hasPermission("utilities.fly.others")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

}
