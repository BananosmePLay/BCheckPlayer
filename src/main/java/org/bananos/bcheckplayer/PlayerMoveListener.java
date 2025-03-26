package org.bananos.bcheckplayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerMoveListener implements Listener {
    private final BCheckPlayer plugin;

    public PlayerMoveListener(BCheckPlayer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (plugin.getCheckManager().isPlayerBeingChecked(player)) {
            // Полная блокировка всех видов движения
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (plugin.getCheckManager().isPlayerBeingChecked(player)) {
            Player checker = plugin.getCheckManager().getCheckPartner(player);
            if (checker != null) {
                plugin.getCheckManager().endCheck(checker, false);
            }
        }
    }
}