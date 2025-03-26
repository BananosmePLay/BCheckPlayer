package org.bananos.bcheckplayer;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class MessageManager {
    private final BCheckPlayer plugin;
    private final FileConfiguration config;

    public MessageManager(BCheckPlayer plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getConfig();
    }

    public String getMessage(String key) {
        String message = config.getString("messages." + key);
        if (message == null) {
            plugin.getLogger().warning("Сообщение не найдено: " + key);
            return ChatColor.RED + "[Ошибка] Сообщение не настроено";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage(String key, String... replacements) {
        String message = getMessage(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i+1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i+1]);
            }
        }
        return message;
    }
}