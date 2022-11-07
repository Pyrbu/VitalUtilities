package lol.pyr.utilities.commands.player;

import lol.pyr.extendedcommands.CommandContext;
import lol.pyr.extendedcommands.api.ExtendedExecutor;
import lol.pyr.extendedcommands.exception.CommandExecutionException;
import lol.pyr.extendedcommands.util.CompletionUtil;
import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.entity.Player;

import java.util.List;

public class ClearInventoryCommand implements ExtendedExecutor<UtilitiesPlugin> {

    @Override
    public void run(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        Player player = context.parseTargetOrSelf("utilities.clearinventory.other");
        player.getInventory().clear();
        player.sendMessage(context.getPlugin().getMessages().get(player, "own-inventory-cleared"));
        if (!context.getSender().equals(player)) context.getSender().sendMessage(context.getPlugin().getMessages().get(player, "other-inventory-cleared", player.getName()));
    }

    @Override
    public List<String> complete(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        if (context.hasPermission("utilities.clearinventory.other") && context.argSize() == 1) return CompletionUtil.players(context.popString());
        return List.of();
    }
}
