package xyz.cozy.cozyclearlag.utils;

import org.bukkit.ChatColor;

public final class MessageUtil {

    private MessageUtil() {
    }

    public static String color(String input) {
        if (input == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String format(String input, Object... placeholders) {
        if (input == null) {
            return "";
        }
        String result = input;
        for (int i = 0; i + 1 < placeholders.length; i += 2) {
            String key = "%" + placeholders[i] + "%";
            String value = String.valueOf(placeholders[i + 1]);
            result = result.replace(key, value);
        }
        return color(result);
    }
}
