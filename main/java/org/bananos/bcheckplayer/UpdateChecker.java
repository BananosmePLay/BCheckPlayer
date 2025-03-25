package org.bananos.bcheckplayer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private final BCheckPlayer plugin;
    private final String currentVersion;
    private String latestVersion;

    public UpdateChecker(BCheckPlayer plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
    }

    public void checkForUpdates() {
        if (!plugin.getConfigManager().isUpdateCheckerEnabled()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        "https://api.github.com/repos/BananosmePLay/BCheckPlayer/releases/latest").openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "BCheckPlayer-Plugin"); // Добавьте эту строку

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                JSONObject response = (JSONObject) new JSONParser().parse(reader);
                latestVersion = ((String) response.get("tag_name")).replace("v", "");

                if (isNewVersionAvailable()) {
                    Bukkit.getScheduler().runTask(plugin, this::notifyAboutUpdate);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Не удалось проверить обновления: " + e.getMessage());
            }
        });
    }

    public boolean isNewVersionAvailable() {
        if (latestVersion == null || currentVersion == null) {
            return false;
        }
        return !currentVersion.equals(latestVersion);
    }

    private void notifyAboutUpdate() {
        if (!plugin.getConfigManager().isUpdateNotifyEnabled()) {
            return;
        }

        UpdateNotifier notifier = new UpdateNotifier(plugin, currentVersion, latestVersion);
        notifier.notifyConsole();
        notifier.notifyAdmins();
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}