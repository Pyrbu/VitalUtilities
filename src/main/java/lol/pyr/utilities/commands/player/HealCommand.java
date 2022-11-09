package lol.pyr.utilities.commands.player;

import lol.pyr.extendedcommands.CommandContext;
import lol.pyr.extendedcommands.api.ExtendedExecutor;
import lol.pyr.extendedcommands.exception.CommandExecutionException;
import lol.pyr.extendedcommands.util.CompletionUtil;
import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.List;

public class HealCommand implements ExtendedExecutor<UtilitiesPlugin> {
    @Override
    @SuppressWarnings("ConstantConditions")
    public void run(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        context.setCurrentUsage(context.getLabel() + (context.hasPermission("utilities.heal.others") ? " <player>" : ""));
        Player player = context.parseTargetOrSelf("utilities.heal.others");
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.sendMessage(context.getPlugin().getMessages().get(player, "own-heal"));
        if (!context.getSender().equals(player)) context.getSender().sendMessage(context.getPlugin().getMessages().get("other-heal", player.getName()));
    }

    @Override
    public List<String> complete(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        if (context.argSize() == 1 && context.hasPermission("utilities.heal.others")) return CompletionUtil.players(context.popString());
        return List.of();
    }
}
