package org.bananos.bcheckplayer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final BCheckPlayer plugin;

    public ReloadCommand(BCheckPlayer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("bcheckplayer.admin")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reloadPluginConfig();
                sender.sendMessage(plugin.getMessageManager().getMessage("reload-success"));
                return true;
            case "update":
                plugin.checkUpdatesCommand(sender);
                return true;
            default:
                sendUsage(sender);
                return true;
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Использование команды /bcheck:");
        sender.sendMessage(ChatColor.YELLOW + "/bcheck reload - Перезагрузить конфигурацию");
        sender.sendMessage(ChatColor.YELLOW + "/bcheck update - Проверить обновления");
    }
}