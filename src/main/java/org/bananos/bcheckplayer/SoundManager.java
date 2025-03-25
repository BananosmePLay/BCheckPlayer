package org.bananos.bcheckplayer;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SoundManager {
    private final BCheckPlayer plugin;

    public SoundManager(BCheckPlayer plugin) {
        this.plugin = plugin;
    }

    public void playSound(Player player, String soundKey) {
        ConfigurationSection soundConfig = plugin.getConfigManager().getSoundConfig(soundKey);
        if (soundConfig == null) return;

        try {
            Sound sound = Sound.valueOf(soundConfig.getString("sound"));
            float volume = (float) soundConfig.getDouble("volume", 1.0);
            float pitch = (float) soundConfig.getDouble("pitch", 1.0);

            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound type: " + soundConfig.getString("sound"));
        }
    }
}