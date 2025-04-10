package org.bananos.bcheckplayer;

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

        initializeMetricsAndDependencies();

        initializeManagers();

        registerCommandsAndEvents();

        getLogger().info("BCheckPlayer enabled");
    }

    private void initializeMetricsAndDependencies() {
        new Metrics(this, 25242);
        setupVault();
        placeholderAPIEnabled = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    private void initializeManagers() {
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.titleManager = new TitleManager(this);
        this.chatManager = new ChatManager(this);
        this.soundManager = new SoundManager(this);
        this.colorManager = new ColorManager();
        this.checkManager = new CheckManager(this);

        this.updateChecker = new UpdateChecker(this);
        if (getConfigManager().isUpdateCheckerEnabled()) {
            this.updateChecker.checkForUpdates();
        }
    }

    private void registerCommandsAndEvents() {
        // Регистрация команд
        getCommand("check").setExecutor(new CheckCommand(this));
        getCommand("bcheck").setExecutor(new ReloadCommand(this));

        // Регистрация обработчиков событий
        registerEventListeners();
    }

    private void registerEventListeners() {
        // Основные слушатели
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Слушатели для ограничений при проверке
        getServer().getPluginManager().registerEvents(new PlayerDropItemListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandPreprocessListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
    }

    public void reloadPluginConfig() {
        reloadConfig();
        configManager.reloadConfig();
        messageManager = new MessageManager(this);
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
        getLogger().info("BCheckPlayer disabled!");
    }

    // Геттеры
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
    public SoundManager getSoundManager() {
        return soundManager;
    }
    public ColorManager getColorManager() {
        return colorManager;
    }
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
    public boolean isPlaceholderAPIEnabled() {return placeholderAPIEnabled;}

}