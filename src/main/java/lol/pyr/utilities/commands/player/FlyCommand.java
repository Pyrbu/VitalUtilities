package lol.pyr.utilities.commands.player;

import lol.pyr.extendedcommands.CommandContext;
import lol.pyr.extendedcommands.api.ExtendedExecutor;
import lol.pyr.extendedcommands.exception.CommandExecutionException;
import lol.pyr.extendedcommands.util.CompletionUtil;
import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.entity.Player;

import java.util.List;

public class FlyCommand implements ExtendedExecutor<UtilitiesPlugin> {
    @Override
    public void run(CommandContext<UtilitiesPlugin> commandContext) throws CommandExecutionException {
        Player player = commandContext.parseTargetOrSelf("utilities.fly.others");
        boolean state = !player.getAllowFlight();
        if (commandContext.argSize() != 0) state = commandContext.parse(Boolean.class);
        player.setAllowFlight(state);
        player.setFlying(state);
        player.sendMessage(commandContext.getPlugin().getMessages().get(player, "own-flying-" + (state ? "enable" : "disable")));
        if (!player.equals(commandContext.getSender())) commandContext.getSender().sendMessage(commandContext.getPlugin().getMessages().get("other-flying-" + (state ? "enable" : "disable")));
    }

    @Override
    public List<String> complete(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        if (!context.hasPermission("utilities.fly.others")) return List.of();
        if (context.argSize() == 1) return CompletionUtil.players(context.popString());
        if (context.argSize() == 2) return CompletionUtil.literal(context.popString(), "true", "false");
        return List.of();
    }
}
