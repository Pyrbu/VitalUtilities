package lol.pyr.utilities.commands.gamemode;

import lol.pyr.extendedcommands.CommandContext;
import lol.pyr.extendedcommands.api.ExtendedExecutor;
import lol.pyr.extendedcommands.exception.CommandExecutionException;
import lol.pyr.extendedcommands.util.CompletionUtil;
import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;

public class GamemodeCommand implements ExtendedExecutor<UtilitiesPlugin> {

    @Override
    public void run(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        context.setCurrentUsage(context.getLabel() + " <gamemode> [player]");
        GameMode gameMode = context.parse(GameMode.class);
        Player player = context.parseTargetOrSelf("utilities.gamemode.others");
        player.setGameMode(gameMode);
        player.sendMessage(context.getPlugin().getMessages().get(player, "own-gamemode-set", gameMode.toString()));
        if (!context.getSender().equals(player)) context.getSender().sendMessage(context.getPlugin().getMessages().get("other-gamemode-set", player.getName(), gameMode.toString()));
    }

    @Override
    public List<String> complete(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        if (context.argSize() == 1) return CompletionUtil.enums(context.popString(), GameMode.values());
        if (context.hasPermission("utilities.gamemode.others") && context.argSize() == 2) return CompletionUtil.players(context.popString());
        return List.of();
    }

}
