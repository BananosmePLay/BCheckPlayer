package org.bananos.bcheckplayer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CheckCommand implements CommandExecutor, TabCompleter {
    private final BCheckPlayer plugin;

    public CheckCommand(BCheckPlayer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("only-players"));
            return true;
        }

        Player checker = (Player) sender;

        if (!checker.hasPermission("bcheckplayer.check")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(plugin.getMessageManager().getMessage("check-usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("player-not-found"));
            return true;
        }

        // Проверка на самого себя
        if (checker.equals(target)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("cannot-check-self"));
            return true;
        }

        // Проверка на байпас
        if (target.hasPermission("bcheckplayer.bypass")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("cannot-check-staff"));
            return true;
        }

        if (args.length > 1 && args[1].equalsIgnoreCase("clear")) {
            if (plugin.getCheckManager().isChecking(checker)) {
                plugin.getCheckManager().clearCheck(checker);
                return true;
            } else {
                sender.sendMessage(plugin.getMessageManager().getMessage("not-in-check"));
                return true;
            }
        }

        plugin.getCheckManager().startCheck(checker, target);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        } else if (args.length == 2 && sender instanceof Player &&
                plugin.getCheckManager().isChecking((Player) sender)) {
            if ("clear".startsWith(args[1].toLowerCase())) {
                completions.add("clear");
            }
        }

        return completions;
    }
}
