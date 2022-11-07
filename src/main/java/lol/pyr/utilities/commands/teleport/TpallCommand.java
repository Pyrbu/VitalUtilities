package lol.pyr.utilities.commands.teleport;

import lol.pyr.extendedcommands.CommandContext;
import lol.pyr.extendedcommands.api.ExtendedExecutor;
import lol.pyr.extendedcommands.exception.CommandExecutionException;
import lol.pyr.extendedcommands.util.CompletionUtil;
import lol.pyr.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class TpallCommand implements ExtendedExecutor<UtilitiesPlugin> {
    @Override
    public void run(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        context.setCurrentUsage(context.getLabel() + " <all|world|permission|radius>");
        Player sender = context.ensureSenderIsPlayer();
        String arg;
        try {
            arg = context.popString();
        } catch (CommandExecutionException e) {
            arg = "all";
        }

        List<? extends Player> targets;
        switch (arg.toLowerCase()) {
            case "all" -> targets = Bukkit.getOnlinePlayers().stream().toList();
            case "world" -> {
                context.setCurrentUsage(context.getLabel() + " world <world>");
                World world = context.parse(World.class);
                targets = Bukkit.getOnlinePlayers().stream()
                        .filter(p -> p.getWorld().equals(world))
                        .toList();
            }
            case "permission" -> {
                context.setCurrentUsage(context.getLabel() + " permission <permission>");
                String permission = context.popString();
                targets = Bukkit.getOnlinePlayers().stream()
                        .filter(p -> p.hasPermission(permission))
                        .toList();
            }
            case "radius" -> {
                context.setCurrentUsage(context.getLabel() + " radius <number>");
                Double radius = context.parse(Double.class);
                targets = Bukkit.getOnlinePlayers().stream()
                        .filter(p -> p.getWorld().equals(sender.getWorld()))
                        .filter(p -> p.getLocation().distance(sender.getLocation()) <= radius)
                        .toList();
            }
            default -> throw new CommandExecutionException("Incorrect usage");
        }

        for (Player p : targets) if (p != sender) p.teleport(sender);
        sender.sendMessage(context.getPlugin().getMessages().get(sender, "tpall", "" + targets.size()));
    }

    @Override
    public List<String> complete(CommandContext<UtilitiesPlugin> context) throws CommandExecutionException {
        if (context.argSize() == 1) return CompletionUtil.literal(context.popString(), "all", "radius", "world", "permission");
        return ExtendedExecutor.super.complete(context);
    }
}
