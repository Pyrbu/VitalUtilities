package lol.pyr.utilities.commands.player;

import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record ClearInventoryCommand(UtilitiesPlugin plugin) implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(plugin.getMessages().get("only-player-command"));
                return true;
            }
            player.getInventory().clear();
            player.sendMessage(plugin.getMessages().get(player, "own-inventory-cleared"));
        } else {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(plugin.getMessages().get("player-not-online", args[0]));
                return true;
            }

            player.getInventory().clear();
            player.sendMessage(plugin.getMessages().get(player, "own-inventory-cleared"));
            sender.sendMessage(plugin.getMessages().get("other-inventory-cleared", player.getName()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender.hasPermission("utilities.clearinventory.others")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

}
