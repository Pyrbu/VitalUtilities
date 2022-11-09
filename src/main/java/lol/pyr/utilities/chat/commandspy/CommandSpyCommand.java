package lol.pyr.utilities.chat.commandspy;

import lol.pyr.extendedcommands.CommandContext;
import lol.pyr.extendedcommands.api.ExtendedExecutor;
import lol.pyr.extendedcommands.exception.CommandExecutionException;
import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.entity.Player;

public class CommandSpyCommand implements ExtendedExecutor<UtilitiesPlugin> {
    @Override
    public void run(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        Player player = context.ensureSenderIsPlayer();
        UtilitiesPlugin plugin = context.getPlugin();
        plugin.runAsync(() -> plugin.getStorage().getUser(player.getUniqueId()).thenAccept((user) -> {
            user.setCommandSpyEnabled(!user.isCommandSpyEnabled());
            if (user.isCommandSpyEnabled()) context.getSender().sendMessage(plugin.getMessages().get(player, "command-spy-enabled"));
            else context.getSender().sendMessage(plugin.getMessages().get(player, "command-spy-disabled"));
        }));
    }
}
