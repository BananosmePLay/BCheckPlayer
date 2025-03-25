package org.bananos.bcheckplayer;

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

        if (args.length > 0 && args[0].equalsIgnoreCase("update")) {
            plugin.checkUpdatesCommand(sender);
            return true;
        }

        plugin.reloadPluginConfig();
        sender.sendMessage(plugin.getMessageManager().getMessage("reload-success"));
        return true;
    }

}