package me.pyr.utilities.broadcast;

import me.pyr.utilities.UtilitiesPlugin;
import me.pyr.utilities.configuration.UtilitiesMessages;
import me.pyr.utilities.util.HexColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record BroadcastCommand(UtilitiesPlugin plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessages().get("incorrect-usage", label + " <message>"));
            return true;
        }
        UtilitiesMessages.PapiMessageRecycler recycler = plugin.getMessages().recycler("broadcast", HexColorUtil.translateFully(String.join(" ", args)));
        plugin.getServer().getLogger().info(recycler.get(null));
        for (Player player : Bukkit.getOnlinePlayers()) player.sendMessage(recycler.get(player));
        return true;
    }

}
