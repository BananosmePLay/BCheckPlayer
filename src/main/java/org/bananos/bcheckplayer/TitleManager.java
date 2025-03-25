package org.bananos.bcheckplayer;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class TitleManager {
    private final BCheckPlayer plugin;

    public TitleManager(BCheckPlayer plugin) {
        this.plugin = plugin;
    }

    public void sendTitle(Player player, String titleKey) {
        ConfigurationSection titleConfig = plugin.getConfigManager().getTitleConfig(titleKey);
        if (titleConfig == null) return;

        String title = ChatColor.translateAlternateColorCodes('&', titleConfig.getString("title", ""));
        String subtitle = ChatColor.translateAlternateColorCodes('&', titleConfig.getString("subtitle", ""));
        int fadeIn = titleConfig.getInt("fadeIn", 10);
        int stay = titleConfig.getInt("stay", 70);
        int fadeOut = titleConfig.getInt("fadeOut", 20);

        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
}