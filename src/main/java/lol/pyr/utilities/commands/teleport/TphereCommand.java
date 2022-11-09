package lol.pyr.utilities.commands.teleport;

import lol.pyr.extendedcommands.CommandContext;
import lol.pyr.extendedcommands.api.ExtendedExecutor;
import lol.pyr.extendedcommands.exception.CommandExecutionException;
import lol.pyr.extendedcommands.util.CompletionUtil;
import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.entity.Player;

import java.util.List;

public class TphereCommand implements ExtendedExecutor<UtilitiesPlugin> {
    @Override
    public void run(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        Player sender = context.ensureSenderIsPlayer();
        Player target = context.parse(Player.class);
        target.teleport(sender.getLocation());
        target.sendMessage(context.getPlugin().getMessages().get(target, "teleported-self", sender.getName()));
        sender.sendMessage(context.getPlugin().getMessages().get(sender, "teleported-to-self", target.getName()));
    }

    @Override
    public List<String> complete(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        if (context.argSize() == 1) return CompletionUtil.players(context.popString());
        return List.of();
    }
}
