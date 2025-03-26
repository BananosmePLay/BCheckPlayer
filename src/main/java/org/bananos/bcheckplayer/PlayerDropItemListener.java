package org.bananos.bcheckplayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {
    private final BCheckPlayer plugin;

    public PlayerDropItemListener(BCheckPlayer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.getCheckManager().isPlayerBeingChecked(player)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMessageManager().getMessage("movement-restricted"));
        }
    }
}