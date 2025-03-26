package org.bananos.bcheckplayer;

import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager {
    private final BCheckPlayer plugin;
    private final Chat vaultChat;
    private final Map<UUID, UUID> activeChatSessions = new HashMap<>();

    public ChatManager(BCheckPlayer plugin) {
        this.plugin = plugin;
        this.vaultChat = plugin.getVaultChat();
    }

    public void startChatSession(Player checker, Player target) {
        activeChatSessions.put(checker.getUniqueId(), target.getUniqueId());
        activeChatSessions.put(target.getUniqueId(), checker.getUniqueId());
    }

    public void endChatSession(Player player) {
        UUID partnerId = activeChatSessions.remove(player.getUniqueId());
        if (partnerId != null) {
            activeChatSessions.remove(partnerId);
        }
    }

    public boolean isInCheckChat(Player player) {
        return activeChatSessions.containsKey(player.getUniqueId());
    }

    public Player getChatPartner(Player player) {
        UUID partnerId = activeChatSessions.get(player.getUniqueId());
        return partnerId != null ? Bukkit.getPlayer(partnerId) : null;
    }

    public String getFormattedMessage(Player sender, Player receiver, String message) {
        String role = plugin.getCheckManager().isChecking(sender) ? "Проверяющий" : "Игрок";

        String format = role.equals("Проверяющий")
                ? plugin.getConfigManager().getCheckerFormat()
                : plugin.getConfigManager().getTargetFormat();

        String formatted = format
                .replace("%vault_prefix%", getVaultPrefix(sender))
                .replace("%player_name%", sender.getName())
                .replace("%message%", message);

        if (plugin.isPlaceholderAPIEnabled() && plugin.getConfigManager().usePlaceholderAPI()) {
            formatted = PlaceholderAPI.setPlaceholders(sender, formatted);
        }

        return ChatColor.translateAlternateColorCodes('&', formatted);
    }

    private String getVaultPrefix(Player player) {
        return vaultChat != null ? vaultChat.getPlayerPrefix(player) : "";
    }
}