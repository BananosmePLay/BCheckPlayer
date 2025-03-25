package org.bananos.bcheckplayer;

import org.bukkit.entity.Player;
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
        Player player = event.getPlayer();
        CheckManager checkManager = plugin.getCheckManager();

        if (checkManager.isPlayerBeingChecked(player) &&
                plugin.getConfigManager().isMovementRestricted()) {

            // Проверяем реальное перемещение (не поворот головы)
            if (event.getFrom().getX() != event.getTo().getX() ||
                    event.getFrom().getY() != event.getTo().getY() ||
                    event.getFrom().getZ() != event.getTo().getZ()) {
                event.setTo(event.getFrom()); // Полная блокировка движения
            }
        }
    }
}