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
    private ColorManager colorManager;

    @Override
    public void onEnable() {
        instance = this;

        new Metrics(this,25242);
        setupVault();

        // Инициализация менеджеров
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.titleManager = new TitleManager(this);
        this.chatManager = new ChatManager(this);
        this.soundManager = new SoundManager(this);
        this.checkManager = new CheckManager(this);
        this.colorManager = new ColorManager();

        // Проверка PlaceholderAPI
        placeholderAPIEnabled = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

        // Инициализация проверки обновлений
        this.updateChecker = new UpdateChecker(this);
        if (getConfigManager().isUpdateCheckerEnabled()) {
            this.updateChecker.checkForUpdates();
        }

        // Регистрация команд и событий
        getCommand("check").setExecutor(new CheckCommand(this));
        getCommand("bcheck").setExecutor(new ReloadCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        getLogger().info("BCheckPlayer v" + getDescription().getVersion() + " успешно запущен!");
    }

    public void checkUpdatesCommand(CommandSender sender) {
        if (updateChecker == null) {
            sender.sendMessage(ChatColor.RED + "Система проверки обновлений не инициализирована");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "[BCheckPlayer] Проверяем обновления...");
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            updateChecker.checkForUpdates();

            Bukkit.getScheduler().runTask(this, () -> {
                if (updateChecker.getLatestVersion() == null) {
                    sender.sendMessage(ChatColor.RED + "[BCheckPlayer] Не удалось проверить обновления");
                    return;
                }

                if (updateChecker.isNewVersionAvailable()) {
                    UpdateNotifier notifier = new UpdateNotifier(
                            this,
                            updateChecker.getCurrentVersion(),
                            updateChecker.getLatestVersion()
                    );
                    sender.sendMessage(notifier.getUpdateMessage());
                } else {
                    sender.sendMessage(ChatColor.GREEN + "[BCheckPlayer] У вас актуальная версия плагина!");
                }
            });
        });
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

    public ColorManager getColorManager() {return colorManager;}

    public UpdateChecker getUpdateChecker() {return updateChecker;}

    public void reloadPluginConfig() {
        reloadConfig();
        configManager.setupConfig();
    }

}