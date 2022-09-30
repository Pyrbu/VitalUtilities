package me.pyr.utilities.util;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexColorUtil {

    private static final Pattern HEX = Pattern.compile("#[0-9a-fA-F]{6}");

    private static String toChatColor(String hexString) {
        StringBuilder builder = new StringBuilder("§x");
        for(Character c : hexString.substring(1).toCharArray()) {
            builder.append("§").append(c);
        }
        return builder.toString();
    }

    public static String translateFully(String string) {
        return ChatColor.translateAlternateColorCodes('&', formatHex(string));
    }

    public static String formatHex(String string) {
        String text = string.replace("&#", "#");
        Matcher matcher = HEX.matcher(string);
        while(matcher.find()) {
            String hexcode = matcher.group();
            text = text.replace(hexcode, toChatColor(hexcode));
        }
        return text;
    }

}