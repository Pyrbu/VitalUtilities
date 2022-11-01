package lol.pyr.utilities.commands.gamemode;

import lol.pyr.utilities.UtilitiesPlugin;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record GamemodeCommand(UtilitiesPlugin plugin) implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.getMessages().get("incorrect-usage", label + " <gamemode> [player]"));
            return true;
        }

        Optional<GameMode> optional = plugin.getUtilitiesConfig().getGameModeAliases().entrySet().stream()
                .filter(e -> e.getValue().contains(args[0].toLowerCase()))
                .map(Map.Entry::getKey).findAny();
        if (optional.isEmpty()) {
            sender.sendMessage(plugin.getMessages().get("incorrect-usage", label + " <gamemode> [player]"));
            return true;
        }

        GameMode gameMode = optional.get();
        if (!sender.hasPermission("utilities.gamemode.others") || args.length < 2) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(plugin.getMessages().get("only-player-command"));
                return true;
            }

            player.setGameMode(gameMode);
            player.sendMessage(plugin.getMessages().get(player, "own-gamemode-set", gameMode.toString()));
            return true;
        }

        Player other = Bukkit.getPlayer(args[2]);
        if (other == null) {
            sender.sendMessage(plugin.getMessages().get("player-not-online", args[2]));
            return true;
        }

        other.setGameMode(gameMode);
        other.sendMessage(plugin.getMessages().get(other, "own-gamemode-set", gameMode.toString()));
        sender.sendMessage(plugin.getMessages().get("other-gamemode-set", other.getName(), gameMode.toString()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            ArrayList<String> completions = new ArrayList<>();
            for (val set : plugin.getUtilitiesConfig().getGameModeAliases().values()) completions.addAll(set);
            return completions;
        }

        if (sender.hasPermission("utilities.gamemode.others") && args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

}
