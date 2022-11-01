package lol.pyr.utilities.commands.gamemode;

import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record GamemodeShortcutCommand(UtilitiesPlugin plugin, GameMode gameMode) implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("utilities.gamemode.others") || args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(plugin.getMessages().get("only-player-command"));
                return true;
            }

            player.setGameMode(gameMode);
            player.sendMessage(plugin.getMessages().get(player, "own-gamemode-set", gameMode.toString()));
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(plugin.getMessages().get("player-not-online", args[0]));
            return true;
        }

        player.setGameMode(gameMode);
        player.sendMessage(plugin.getMessages().get(player, "own-gamemode-set", gameMode.toString()));
        sender.sendMessage(plugin.getMessages().get("other-gamemode-set", player.getName(), gameMode.toString()));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender.hasPermission("utilities.gamemode.others")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

}
