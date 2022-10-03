package me.pyr.utilities.misc;

import me.pyr.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record TpallCommand(UtilitiesPlugin plugin) implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessages().get("only-player-command"));
            return true;
        }
        TpallType type = TpallType.ALL;
        Float radius = null;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("world")) type = TpallType.WORLD;
            else if (args[0].equalsIgnoreCase("radius")) {
                if (args.length != 2) {
                    sender.sendMessage(plugin.getMessages().get(player, "incorrect-usage", label + " radius <number>"));
                    return true;
                }
                type = TpallType.RADIUS;
                try {
                    radius = Float.parseFloat(args[1]);
                } catch (IllegalArgumentException exception) {
                    sender.sendMessage(plugin.getMessages().get(player, "incorrect-usage", label + " radius <number>"));
                    return true;
                }
            }
        }
        switch (type) {
            case ALL -> {
                for (Player p : Bukkit.getOnlinePlayers()) if (p != player) p.teleport(player);
                sender.sendMessage(plugin.getMessages().get(player, "tpall"));
            }
            case WORLD -> {
                for (Player p : Bukkit.getOnlinePlayers()) if (p != player && p.getWorld() == player.getWorld()) p.teleport(player);
                sender.sendMessage(plugin.getMessages().get(player, "tpall-world"));
            }
            case RADIUS -> {
                for (Player p : Bukkit.getOnlinePlayers())
                    if (p != player && p.getWorld() == player.getWorld() && p.getLocation().distance(player.getLocation()) <= radius)
                        p.teleport(player);
                sender.sendMessage(plugin.getMessages().get(player, "tpall-radius", radius.toString()));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Stream.of("all", "radius", "world")
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private enum TpallType {
        ALL, WORLD, RADIUS
    }
}
