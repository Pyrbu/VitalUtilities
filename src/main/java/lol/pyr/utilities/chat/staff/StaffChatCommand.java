package lol.pyr.utilities.chat.staff;

import lol.pyr.extendedcommands.CommandContext;
import lol.pyr.extendedcommands.api.ExtendedExecutor;
import lol.pyr.extendedcommands.exception.CommandExecutionException;
import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StaffChatCommand implements ExtendedExecutor<UtilitiesPlugin> {
    @Override
    public void run(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        context.ensureArgsNotEmpty();
        String message = context.dumpAllArgs();
        if (context.getPlugin().getUtilitiesConfig().isEnableNetworkFeatures()) context.getPlugin().getNetworkConnection().broadcastWithPermission("utilities.staffchat", message);
        else for (Player p : Bukkit.getOnlinePlayers()) if (p.hasPermission("utilities.staffchat")) p.sendMessage(message);
    }
}
