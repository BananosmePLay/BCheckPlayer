package org.bananos.bcheckplayer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CheckManager {
    private final BCheckPlayer plugin;
    private final TitleManager titleManager;
    private final Map<UUID, CheckSession> activeChecks = new HashMap<>();
    private final Map<UUID, Long> lastActivity = new HashMap<>();

    public CheckManager(BCheckPlayer plugin) {
        this.plugin = plugin;
        this.titleManager = plugin.getTitleManager();
        startInactivityCheckTask();
    }

    private void startInactivityCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkInactivePlayers();
            }
        }.runTaskTimer(plugin, 0, 20 * 60 * 5);
    }

    public void startCheck(Player checker, Player target) {
        if (activeChecks.containsKey(checker.getUniqueId())) {
            endCheck(checker, false);
        }

        CheckSession session = new CheckSession(checker, target);
        activeChecks.put(checker.getUniqueId(), session);

        Location checkLocation = plugin.getConfigManager().getCheckLocation();
        target.teleport(checkLocation);

        checker.sendMessage(plugin.getMessageManager().getMessage("check-started", "%player%", target.getName()));
        titleManager.sendTitle(target, "target-notification");
        plugin.getSoundManager().playSound(target, "check-start");

        // Отправляем все инструкции сразу
        String[] instructions = plugin.getConfigManager().getInstructions();
        for (String instruction : instructions) {
            if (instruction != null && !instruction.isEmpty()) {
                target.sendMessage(ColorManager.translate(instruction));
            }
        }

        // Инициализируем задачу для автоматического завершения
        if (plugin.getConfigManager().isAutoEndCheck()) {
            // Используем отдельную задержку для автозавершения (не instructionDelay!)
            int autoEndDelay = 20 * 60 * 5; // Например, 5 минут (в тиках)

            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    endCheck(checker, true);
                }
            }.runTaskLater(plugin, autoEndDelay);

            session.setInstructionTask(task);
        }

        plugin.getChatManager().startChatSession(checker, target);
    }

    public void clearCheck(Player checker) {
        CheckSession session = activeChecks.get(checker.getUniqueId());
        if (session != null) {
            Player target = session.getTarget();
            titleManager.sendTitle(target, "player-cleared");
            // Воспроизводим звук только у проверяемого игрока
            plugin.getSoundManager().playSound(target, "check-end");
            endCheck(checker, false);
        }
    }

    private void startInstructions(CheckSession session) {
        int autoEndDelay = plugin.getConfigManager().getInt("settings.auto-end-delay") * 20;
        String[] instructions = plugin.getConfigManager().getInstructions();

        BukkitTask task = new BukkitRunnable() {
            private int current = 0;

            @Override
            public void run() {
                while (current < instructions.length &&
                        (instructions[current] == null || instructions[current].isEmpty())) {
                    current++;
                }

                if (current < instructions.length) {
                    session.getTarget().sendMessage(instructions[current]);
                    current++;
                } else {
                    if (plugin.getConfigManager().isAutoEndCheck()) {
                        endCheck(session.getChecker(), true);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, delay, delay);

        session.setInstructionTask(task);
    }

    public void endCheck(Player checker, boolean notify) {
        CheckSession session = activeChecks.remove(checker.getUniqueId());
        if (session != null) {
            // Добавляем проверку на null перед отменой задачи
            if (session.getInstructionTask() != null) {
                session.getInstructionTask().cancel();
            }
            lastActivity.remove(session.getTarget().getUniqueId());
            plugin.getChatManager().endChatSession(checker);

            // Воспроизводим звук только у проверяемого игрока
            plugin.getSoundManager().playSound(session.getTarget(), "check-end");

            if (notify) {
                checker.sendMessage(plugin.getMessageManager().getMessage("check-complete"));
            }
        }
    }

    public void checkInactivePlayers() {
        long now = System.currentTimeMillis();
        long timeout = plugin.getConfigManager().getInactivityTimeout() * 60 * 1000;

        activeChecks.entrySet().removeIf(entry -> {
            Player checker = Bukkit.getPlayer(entry.getKey());
            Player target = entry.getValue().getTarget();

            // Если игрок вышел или неактивен более 24 часов
            if (target == null || !target.isOnline() ||
                    (lastActivity.getOrDefault(target.getUniqueId(), 0L) + timeout < now)) {

                // Тихий выход без уведомлений
                if (checker != null && checker.isOnline()) {
                    activeChecks.remove(entry.getKey());
                    lastActivity.remove(target.getUniqueId());
                    plugin.getChatManager().endChatSession(checker);
                }
                return true;
            }
            return false;
        });
    }

    public void updatePlayerActivity(Player player) {
        if (isPlayerBeingChecked(player)) {
            lastActivity.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    public boolean isPlayerBeingChecked(Player player) {
        return activeChecks.values().stream()
                .anyMatch(s -> s.getTarget().getUniqueId().equals(player.getUniqueId()));
    }

    public boolean isChecking(Player player) {
        return activeChecks.containsKey(player.getUniqueId());
    }

    public boolean isInCheck(Player player) {
        return isChecking(player) || isPlayerBeingChecked(player);
    }

    public Player getCheckPartner(Player player) {
        if (isChecking(player)) {
            return activeChecks.get(player.getUniqueId()).getTarget();
        } else if (isPlayerBeingChecked(player)) {
            for (Map.Entry<UUID, CheckSession> entry : activeChecks.entrySet()) {
                if (entry.getValue().getTarget().getUniqueId().equals(player.getUniqueId())) {
                    return Bukkit.getPlayer(entry.getKey());
                }
            }
        }
        return null;
    }

    public boolean isMovementRestricted() {
        return plugin.getConfigManager().isMovementRestricted();
    }

    private static class CheckSession {
        private final Player checker;
        private final Player target;
        private BukkitTask instructionTask;

        public CheckSession(Player checker, Player target) {
            this.checker = checker;
            this.target = target;
        }

        public Player getChecker() {
            return checker;
        }

        public Player getTarget() {
            return target;
        }

        public BukkitTask getInstructionTask() {
            return instructionTask;
        }

        public void setInstructionTask(BukkitTask instructionTask) {
            this.instructionTask = instructionTask;
        }
    }
}