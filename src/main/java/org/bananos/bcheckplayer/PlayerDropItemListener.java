package org.bananos.bcheckplayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (plugin.getCheckManager().isPlayerBeingChecked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}