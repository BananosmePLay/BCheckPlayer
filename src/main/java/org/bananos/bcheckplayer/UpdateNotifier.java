package org.bananos.bcheckplayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UpdateNotifier {
    private final BCheckPlayer plugin;
    private final String currentVersion;
    private final String latestVersion;
    private final String downloadUrl;

    public UpdateNotifier(BCheckPlayer plugin, String currentVersion, String latestVersion) {
        this.plugin = plugin;
        this.currentVersion = currentVersion;
        this.latestVersion = latestVersion;
        this.downloadUrl = "https://github.com/BananosmePLay/BCheckPlayer/releases";
    }

    public void notifyConsole() {
        String message = String.format(
                "[BCheckPlayer] Доступно обновление %s (у вас %s)%nСкачайте новую версию: %s",
                latestVersion, currentVersion, downloadUrl
        );
        plugin.getLogger().warning(message);
    }

    public void notifyAdmins() {
        String message = ChatColor.translateAlternateColorCodes('&', String.format(
                "&e[BCheckPlayer] &fДоступно обновление &a%s &f(у вас &c%s&f)%n&fСкачайте: &b%s",
                latestVersion, currentVersion, downloadUrl
        ));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("bcheckplayer.admin")) {
                player.sendMessage(message);
            }
        }
    }

    public String getUpdateMessage() {
        return ChatColor.translateAlternateColorCodes('&', String.format(
                "&e[BCheckPlayer] &fДоступно обновление!%n" +
                        "&fТекущая версия: &c%s%n" +
                        "&fНовая версия: &a%s%n" +
                        "&fСкачать: &b%s",
                currentVersion, latestVersion, downloadUrl
        ));
    }
}