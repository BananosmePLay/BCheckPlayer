package org.bananos.bcheckplayer;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageManager {
    private final FileConfiguration config;

    public MessageManager(JavaPlugin plugin) {
        this.config = plugin.getConfig();
    }

    public String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("messages." + key, "&cСообщение не найдено: " + key));
    }

    public String getMessage(String key, String placeholder, String replacement) {
        return getMessage(key).replace(placeholder, replacement);
    }
}