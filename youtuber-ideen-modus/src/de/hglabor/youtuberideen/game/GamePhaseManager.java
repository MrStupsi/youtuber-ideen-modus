package de.hglabor.youtuberideen.game;

import de.hglabor.youtuberideen.YoutuberIdeen;
import de.hglabor.youtuberideen.game.phases.LobbyPhase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class GamePhaseManager extends IGamePhaseManager {
    private static Map<UUID, User> users = new HashMap<>();
    private static AtomicInteger timer = new AtomicInteger();
    public static AbstractGamePhase phase;
    public static void run() {
        Bukkit.getScheduler().runTaskTimer(YoutuberIdeen.INSTANCE, () -> {
            phase.tick(timer.getAndIncrement());
        }, 0, 1);
    }
    public void resetTimer() {
        timer.set(0);
    }

    public GamePhaseManager() {
        phase = new LobbyPhase(this);
        run();
    }

    public static class User {
        public UUID uuid;
        public Vector spawnLocation = null;

        public User(UUID uuid) {
            this.uuid = uuid;
        }
    }

    public static User get(Player p) {
        if (!users.containsKey(p.getUniqueId())) users.put(p.getUniqueId(), new User(p.getUniqueId()));
        return users.get(p.getUniqueId());
    }
}
