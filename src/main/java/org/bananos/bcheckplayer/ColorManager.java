package org.bananos.bcheckplayer;

import org.bukkit.ChatColor;

public class ColorManager {
    public static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}