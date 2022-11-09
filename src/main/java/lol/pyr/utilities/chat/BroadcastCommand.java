package lol.pyr.utilities.chat;

import lol.pyr.extendedcommands.CommandContext;
import lol.pyr.extendedcommands.api.ExtendedExecutor;
import lol.pyr.extendedcommands.exception.CommandExecutionException;
import lol.pyr.utilities.UtilitiesPlugin;
import lol.pyr.utilities.configuration.UtilitiesMessages;
 import lol.pyr.utilities.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BroadcastCommand implements ExtendedExecutor<UtilitiesPlugin> {
    @Override
    public void run(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        context.setCurrentUsage(context.getLabel() + " <message>");
        context.ensureArgsNotEmpty();
        UtilitiesMessages.PapiMessageRecycler recycler = context.getPlugin().getMessages().recycler("broadcast", ColorUtil.translateFully(context.dumpAllArgs()));
        context.getPlugin().getServer().getLogger().info(recycler.get(null));
        for (Player player : Bukkit.getOnlinePlayers()) player.sendMessage(recycler.get(player));
    }
}
