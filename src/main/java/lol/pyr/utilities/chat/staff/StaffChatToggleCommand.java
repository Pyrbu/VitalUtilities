package lol.pyr.utilities.chat.staff;

import lol.pyr.extendedcommands.CommandContext;
import lol.pyr.extendedcommands.api.ExtendedExecutor;
import lol.pyr.extendedcommands.exception.CommandExecutionException;
import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.entity.Player;

public class StaffChatToggleCommand implements ExtendedExecutor<UtilitiesPlugin> {
    @Override
    public void run(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        Player player = context.ensureSenderIsPlayer();
        context.getPlugin().runAsync(() -> context.getPlugin().getStorage().getUser(player.getUniqueId()).thenAccept((user) -> {
            user.setStaffChatToggled(!user.isStaffChatToggled());
            if (user.isStaffChatToggled()) player.sendMessage(context.getPlugin().getMessages().get(player, "staffchat-enabled"));
            else player.sendMessage(context.getPlugin().getMessages().get(player, "staffchat-disabled"));
        }));
    }
}
