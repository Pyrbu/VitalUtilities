package lol.pyr.utilities.chat;

import lol.pyr.extendedcommands.CommandContext;
import lol.pyr.extendedcommands.api.ExtendedExecutor;
import lol.pyr.extendedcommands.exception.CommandExecutionException;
import lol.pyr.utilities.UtilitiesPlugin;
import lol.pyr.utilities.util.ColorUtil;


public class NetworkBroadcastCommand implements ExtendedExecutor<UtilitiesPlugin> {
    @Override
    public void run(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        if (!context.getPlugin().getUtilitiesConfig().isEnableNetworkFeatures()) {
            context.getSender().sendMessage(context.getPlugin().getMessages().get("no-network-features"));
            return;
        }
        context.setCurrentUsage(context.getLabel() + " <message>");
        context.ensureArgsNotEmpty();
        context.getPlugin().getNetworkConnection().broadcastMessage(context.getPlugin().getMessages().get("network-broadcast", ColorUtil.translateFully(context.dumpAllArgs())));
    }

}
