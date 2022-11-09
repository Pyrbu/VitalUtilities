package lol.pyr.utilities.commands.player;

import lol.pyr.extendedcommands.CommandContext;
import lol.pyr.extendedcommands.api.ExtendedExecutor;
import lol.pyr.extendedcommands.exception.CommandExecutionException;
import lol.pyr.extendedcommands.util.CompletionUtil;
import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.entity.Player;

import java.util.List;

public class SudoCommand implements ExtendedExecutor<UtilitiesPlugin> {
    @Override
    public void run(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        context.setCurrentUsage(context.getLabel() + " <player> <command>");
        Player player = context.parse(Player.class);
        context.ensureArgsNotEmpty();
        String cmd = context.dumpAllArgs();
        if (cmd.toLowerCase().startsWith("c:")) player.chat(cmd.substring(2));
        else player.performCommand(cmd);
        context.getPlugin().getMessages().get("sudo", player.getName(), cmd);
    }

    @Override
    public List<String> complete(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        if (context.argSize() == 1) return CompletionUtil.players(context.popString());
        return List.of();
    }
}
