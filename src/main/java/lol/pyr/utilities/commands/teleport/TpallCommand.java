package lol.pyr.utilities.commands.teleport;

import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
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
        World world = player.getWorld();
        String permission = "";
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("world")) {
                type = TpallType.WORLD;
                if (args.length > 1) {
                    world = Bukkit.getWorld(args[1]);
                    if (world == null) {
                        sender.sendMessage(plugin.getMessages().get(player, "no-world-exists", args[1]));
                        return true;
                    }
                }
            }
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
            else if (args[0].equalsIgnoreCase("permission")) {
                if (args.length != 2) {
                    sender.sendMessage(plugin.getMessages().get(player, "incorrect-usage", label + " permission <permission>"));
                    return true;
                }
                type = TpallType.PERMISSION;
                permission = args[1];
            }
        }
        int count = 0;
        switch (type) {
            case ALL -> {
                for (Player p : Bukkit.getOnlinePlayers()) if (p != player) {
                    p.teleport(player);
                    count++;
                }
            }
            case WORLD -> {
                for (Player p : Bukkit.getOnlinePlayers()) if (p != player && world == p.getWorld()) {
                    p.teleport(player);
                    count++;
                }
            }
            case RADIUS -> {
                for (Player p : Bukkit.getOnlinePlayers())
                    if (p != player && p.getWorld() == player.getWorld() && p.getLocation().distance(player.getLocation()) <= radius) {
                        p.teleport(player);
                        count++;
                    }
            }
            case PERMISSION -> {
                for (Player p : Bukkit.getOnlinePlayers())
                    if (p != player && p.hasPermission(permission)) {
                        p.teleport(player);
                        count++;
                    }
            }
        }
        sender.sendMessage(plugin.getMessages().get(player, "tpall", "" + count));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Stream.of("all", "radius", "world", "permission")
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("world")) {
            return Bukkit.getWorlds().stream()
                    .map(World::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private enum TpallType {
        ALL, WORLD, RADIUS, PERMISSION
    }
}
