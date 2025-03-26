package org.bananos.bcheckplayer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandPreprocessListener implements Listener {
    private final BCheckPlayer plugin;
    private static final String[] ALLOWED_CMDS = {"/msg ", "/tell ", "/w "};

    public PlayerCommandPreprocessListener(BCheckPlayer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (plugin.getCheckManager().isPlayerBeingChecked(event.getPlayer())) {
            String cmd = event.getMessage().toLowerCase();
            boolean allowed = false;

            for (String allowedCmd : ALLOWED_CMDS) {
                if (cmd.startsWith(allowedCmd)) {
                    allowed = true;
                    break;
                }
            }

            if (!allowed) {
                event.setCancelled(true);
            }
        }
    }
}