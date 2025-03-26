package org.bananos.bcheckplayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {
    private final BCheckPlayer plugin;

    public PlayerChatListener(BCheckPlayer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (plugin.getCheckManager().isInCheck(player)) {
            event.setCancelled(true);
            Player partner = plugin.getCheckManager().getCheckPartner(player);
            if (partner != null) {
                // Для партнера сообщение форматируется с точки зрения отправителя
                partner.sendMessage(plugin.getChatManager().getFormattedMessage(player, partner, event.getMessage()));
                // Для самого игрока сообщение форматируется с его точки зрения
                player.sendMessage(plugin.getChatManager().getFormattedMessage(player, player, event.getMessage()));
            }
        }
    }
}