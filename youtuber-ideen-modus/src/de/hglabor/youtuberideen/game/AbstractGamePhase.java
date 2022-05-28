package de.hglabor.youtuberideen.game;

import de.hglabor.youtuberideen.YoutuberIdeen;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGamePhase {
    protected IGamePhaseManager gamePhaseManager;
    public List<Listener> listeners = new ArrayList<>();
    public List<Tickable> tickable = new ArrayList<>();

    public abstract void tick(int tick);
    public abstract AbstractGamePhase nextPhase();

    protected void startPhase() {
        listeners.forEach((x) -> Bukkit.getPluginManager().registerEvents(x, YoutuberIdeen.INSTANCE));
    }

    protected void startNextPhase() {
        listeners.forEach((x) -> HandlerList.unregisterAll(x));
        GamePhaseManager.phase = nextPhase();
    }

    protected void broadcast(String text) {
        Bukkit.broadcastMessage(text);
    }

    protected void executeTickables() {
        tickable.forEach((x) -> x.onTick());
    }
}
