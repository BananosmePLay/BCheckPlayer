package org.bananos.bcheckplayer;

import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.chat.Chat;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class BCheckPlayer extends JavaPlugin {
    private static BCheckPlayer instance;
    private CheckManager checkManager;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private ChatManager chatManager;
    private TitleManager titleManager;
    private Chat vaultChat;
    private boolean placeholderAPIEnabled;
    private SoundManager soundManager;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        instance = this;

        // Инициализация bStats
        new Metrics(this, 12345);

        // Настройка Vault
        setupVault();

        // Инициализация проверки обновлений
        this.updateChecker = new UpdateChecker(this);
        if (getConfigManager().isUpdateCheckerEnabled()) {
            this.updateChecker.checkForUpdates();
        }

        this.soundManager = new SoundManager(this);

        // Проверка PlaceholderAPI
        placeholderAPIEnabled = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

        // Инициализация менеджеров
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.titleManager = new TitleManager(this);
        this.chatManager = new ChatManager(this);
        this.checkManager = new CheckManager(this);

        // Регистрация команд и событий
        getCommand("check").setExecutor(new CheckCommand(this));
        getCommand("bcheck").setExecutor(new ReloadCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);

        getLogger().info("BCheckPlayer v" + getDescription().getVersion() + " успешно запущен!");
    }

    private void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault не найден! Префиксы не будут работать.");
            return;
        }

        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp != null) {
            vaultChat = rsp.getProvider();
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("BCheckPlayer отключен!");
    }

    public static BCheckPlayer getInstance() {
        return instance;
    }

    public CheckManager getCheckManager() {
        return checkManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public TitleManager getTitleManager() {
        return titleManager;
    }

    public Chat getVaultChat() {
        return vaultChat;
    }

    public boolean isPlaceholderAPIEnabled() {
        return placeholderAPIEnabled;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public void reloadPluginConfig() {
        reloadConfig();
        configManager.setupConfig();
    }

    public void checkUpdatesCommand(CommandSender sender) {
        if (updateChecker.getLatestVersion() == null) {
            sender.sendMessage(ChatColor.YELLOW + "[BCheckPlayer] Проверяем обновления...");
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                updateChecker.checkForUpdates();
                if (updateChecker.getLatestVersion() != null) {
                    Bukkit.getScheduler().runTask(this, () -> {
                        if (updateChecker.isNewVersionAvailable()) {
                            sender.sendMessage(new UpdateNotifier(this,
                                    updateChecker.getCurrentVersion(),
                                    updateChecker.getLatestVersion()).getUpdateMessage());
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "[BCheckPlayer] У вас актуальная версия плагина!");
                        }
                    });
                }
            });
        } else {
            if (updateChecker.isNewVersionAvailable()) {
                sender.sendMessage(new UpdateNotifier(this,
                        updateChecker.getCurrentVersion(),
                        updateChecker.getLatestVersion()).getUpdateMessage());
            } else {
                sender.sendMessage(ChatColor.GREEN + "[BCheckPlayer] У вас актуальная версия плагина!");
            }
        }
    }
}