package org.bananos.bcheckplayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final BCheckPlayer plugin;

    public PlayerJoinListener(BCheckPlayer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (plugin.getUpdateChecker() != null &&
                plugin.getUpdateChecker().isNewVersionAvailable() &&
                player.hasPermission("bcheckplayer.admin")) {

            player.sendMessage(plugin.getUpdateChecker().getUpdateNotifier().getUpdateMessage());
        }
    }
}