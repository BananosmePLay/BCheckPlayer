package org.bananos.bcheckplayer;

import org.bukkit.ChatColor;

public class ColorManager {
    public static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String translate(String text, Object... args) {
        return translate(String.format(text, args));
    }
}