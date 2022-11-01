package lol.pyr.utilities.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;

import java.util.List;
import java.util.stream.Collectors;

public class CompletionUtil {

    public static List<String> players(String input) {
        String finalInput = input.toLowerCase();
        return Bukkit.getOnlinePlayers().stream()
                .map(HumanEntity::getName)
                .filter(s -> s.toLowerCase().startsWith(finalInput))
                .collect(Collectors.toList());
    }

}
