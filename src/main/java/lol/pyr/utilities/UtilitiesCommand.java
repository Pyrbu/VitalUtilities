package lol.pyr.utilities;

import lol.pyr.extendedcommands.CommandContext;
import lol.pyr.extendedcommands.api.ExtendedExecutor;
import lol.pyr.extendedcommands.exception.CommandExecutionException;
import lol.pyr.extendedcommands.util.CompletionUtil;
import org.bukkit.ChatColor;

import java.util.List;

public class UtilitiesCommand implements ExtendedExecutor<UtilitiesPlugin> {
    @Override
    public void run(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        context.setCurrentUsage(context.getLabel() + " help");
        String arg = context.popString();

        if (arg.equalsIgnoreCase("reload")) {
            long before = System.currentTimeMillis();
            context.getPlugin().getUtilitiesConfig().reload();
            context.getPlugin().getMessages().reload();
            context.getSender().sendMessage(ChatColor.GREEN + "All configurations & storage have been reloaded (" + (System.currentTimeMillis() - before) + "ms)");
        }
        else {
            context.getSender().sendMessage(ChatColor.GREEN + "Available subcommands:\n" +
                    " /" + context.getLabel() + " reload - Reloads all configurations & storage");
        }
    }

    @Override
    public List<String> complete(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        if (context.argSize() == 1) return CompletionUtil.literal(context.popString(), "reload", "help");
        return List.of();
    }
}
