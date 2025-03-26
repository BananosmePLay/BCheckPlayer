package org.bananos.bcheckplayer;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    private final BCheckPlayer plugin;

    public PlayerMoveListener(BCheckPlayer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (plugin.getCheckManager().isPlayerBeingChecked(event.getPlayer())) {
            if (hasChangedPosition(event.getFrom(), event.getTo())) {
                event.setTo(event.getFrom());
            }
        }
    }

    private boolean hasChangedPosition(Location from, Location to) {
        return from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ();
    }
}