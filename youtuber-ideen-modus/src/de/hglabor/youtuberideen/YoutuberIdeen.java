package de.hglabor.youtuberideen;

import de.hglabor.youtuberideen.game.GamePhaseManager;
import de.hglabor.youtuberideen.wichtiger.SkyIslandGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class YoutuberIdeen extends JavaPlugin {
    public static YoutuberIdeen INSTANCE = null;
    public static final String PREFIX = "§7[§cYouTuber§f-Ideen§7] ";

    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        new SkyIslandGenerator();
        new GamePhaseManager();
        getLogger().info("The Plugin was enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("The Plugin was disabled!");
    }
}
