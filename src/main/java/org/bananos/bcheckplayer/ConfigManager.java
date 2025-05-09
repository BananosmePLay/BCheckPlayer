package org.bananos.bcheckplayer;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setupConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public void setupConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        config.addDefault("check-location.world", "world");
        config.addDefault("check-location.x", 0);
        config.addDefault("check-location.y", 70);
        config.addDefault("check-location.z", 0);
        config.addDefault("check-location.yaw", 0);
        config.addDefault("check-location.pitch", 0);

        config.addDefault("formats.checker-format", "&e[ &bПроверяющий &e] &r%vault_prefix% &f%player_name% &f>> &f%message%");
        config.addDefault("formats.target-format", "&e[ &bИгрок &e] &r%vault_prefix% &f%player_name% &f>> &f%message%");

        config.addDefault("messages.no-permission", "&cУ вас нет прав на эту команду!");
        config.addDefault("messages.only-players", "&cТолько игроки могут использовать эту команду!");
        config.addDefault("messages.check-usage", "&eИспользование: /check <ник> [clear]");
        config.addDefault("messages.cant-use-commands", "&cВы не можете использовать команды во время проверки!");
        config.addDefault("messages.player-not-found", "&cИгрок не найден!");
        config.addDefault("messages.check-started", "&aВы начали проверку игрока %player%");
        config.addDefault("messages.check-complete", "&aПроверка завершена!");
        config.addDefault("messages.player-cleared", "&aВы свободны!");
        config.addDefault("messages.not-in-check", "&cВы не проводите проверку!");
        config.addDefault("messages.instructions", new String[]{
                "&6╔ &fПроверка на читы!",
                "&6╚ &fПишите свой &1&lDiscord &fили код &c&lAnyDesk"
        });

        config.addDefault("sounds.check-start.sound", "ENTITY_RAVAGER_DEATH");
        config.addDefault("sounds.check-start.volume", 1.0);
        config.addDefault("sounds.check-start.pitch", 1.0);

        config.addDefault("sounds.check-end.sound", "ENTITY_PLAYER_LEVELUP");
        config.addDefault("sounds.check-end.volume", 1.0);
        config.addDefault("sounds.check-end.pitch", 1.0);

        config.addDefault("titles.target-notification.title", "&cПРОВЕРКА");
        config.addDefault("titles.target-notification.subtitle", "&fСледуйте инструкциям проверяющего");
        config.addDefault("titles.target-notification.fadeIn", 10);
        config.addDefault("titles.target-notification.stay", 70);
        config.addDefault("titles.target-notification.fadeOut", 20);

        config.addDefault("titles.player-cleared.title", "&aПРОВЕРКА ПРОЙДЕНА");
        config.addDefault("titles.player-cleared.subtitle", "&fВы признаны чистым");
        config.addDefault("titles.player-cleared.fadeIn", 10);
        config.addDefault("titles.player-cleared.stay", 70);
        config.addDefault("titles.player-cleared.fadeOut", 20);

        config.addDefault("settings.inactivity-timeout", 1440);
        config.addDefault("settings.restrict-movement", true);
        config.addDefault("settings.use-placeholderapi", true);
        config.addDefault("settings.auto-end-check", true);
        config.addDefault("settings.update-checker", true);
        config.addDefault("settings.update-notify", true);
        config.addDefault("settings.auto-end-delay", 300);
        config.addDefault("settings.restrict-building", true);
        config.addDefault("settings.restrict-interactions", true);

        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public Location getCheckLocation() {
        return new Location(
                plugin.getServer().getWorld(config.getString("check-location.world")),
                config.getDouble("check-location.x"),
                config.getDouble("check-location.y"),
                config.getDouble("check-location.z"),
                (float) config.getDouble("check-location.yaw"),
                (float) config.getDouble("check-location.pitch")
        );
    }

    public String getCheckerFormat() {
        return config.getString("formats.checker-format");
    }

    public String getTargetFormat() {
        return config.getString("formats.target-format");
    }

    public int getInactivityTimeout() {
        return config.getInt("settings.inactivity-timeout");
    }

    public ConfigurationSection getSoundConfig(String soundKey) {
        return config.getConfigurationSection("sounds." + soundKey);
    }

    public boolean isUpdateCheckerEnabled() {
        return config.getBoolean("settings.update-checker", true);
    }

    public boolean isUpdateNotifyEnabled() {
        return config.getBoolean("settings.update-notify", true);
    }

    public boolean isMovementRestricted() {
        return config.getBoolean("settings.restrict-movement");
    }

    public boolean usePlaceholderAPI() {
        return config.getBoolean("settings.use-placeholderapi");
    }

    public boolean isAutoEndCheck() {
        return config.getBoolean("settings.auto-end-check");
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public boolean isBuildingRestricted() {
        return config.getBoolean("settings.restrict-building", true);
    }

    public boolean isInteractionsRestricted() {
        return config.getBoolean("settings.restrict-interactions", true);
    }

    public String[] getInstructions() {
        return config.getStringList("messages.instructions").toArray(new String[0]);
    }

    public ConfigurationSection getTitleConfig(String titleKey) {
        return config.getConfigurationSection("titles." + titleKey);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }
}