package lol.pyr.utilities.commands.player;

import lol.pyr.extendedcommands.CommandContext;
import lol.pyr.extendedcommands.api.ExtendedExecutor;
import lol.pyr.extendedcommands.exception.CommandExecutionException;
import lol.pyr.extendedcommands.util.CompletionUtil;
import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.entity.Player;

import java.util.List;

public class FeedCommand implements ExtendedExecutor<UtilitiesPlugin> {

    @Override
    public void run(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        context.setCurrentUsage(context.getLabel() + (context.hasPermission("utilities.feed.others") ? " <player>" : ""));
        Player player = context.parseTargetOrSelf("utilities.feed.others");
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExhaustion(0);
        player.sendMessage(context.getPlugin().getMessages().get(player, "own-feed"));
        if (!context.getSender().equals(player)) context.getSender().sendMessage(context.getPlugin().getMessages().get("other-feed", player.getName()));
    }

    @Override
    public List<String> complete(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        if (context.argSize() == 1 && context.hasPermission("utilities.feed.others")) return CompletionUtil.players(context.popString());
        return List.of();
    }
}
