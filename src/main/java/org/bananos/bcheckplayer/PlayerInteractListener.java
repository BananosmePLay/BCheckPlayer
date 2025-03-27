package org.bananos.bcheckplayer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    private final BCheckPlayer plugin;

    public PlayerInteractListener(BCheckPlayer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (plugin.getCheckManager().isPlayerBeingChecked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (plugin.getCheckManager().isPlayerBeingChecked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}