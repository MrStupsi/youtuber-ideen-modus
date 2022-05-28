package de.hglabor.youtuberideen.game.phases;

import de.hglabor.youtuberideen.game.AbstractGamePhase;
import de.hglabor.youtuberideen.game.IGamePhaseManager;
import de.hglabor.youtuberideen.holzkopf.BannerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EndPhase extends AbstractGamePhase {
    private int closeTime = 10;

    public EndPhase(IGamePhaseManager gamePhaseManager, Player winner) {
        this.gamePhaseManager = gamePhaseManager;
        listeners.add(new Listener() {
            @EventHandler
            public void event(PlayerJoinEvent e) {
                e.setJoinMessage(null);
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
            }
        });
        listeners.add(new Listener() {
            @EventHandler
            public void event(PlayerQuitEvent e) {
                e.setQuitMessage(null);
                if (e.getPlayer().getGameMode() != GameMode.SPECTATOR) {
                    Bukkit.broadcastMessage(ChatColor.RED + e.getPlayer().getName() + " hat das Spiel verlassen.");
                }
            }
        });
        listeners.add(new Listener() {
            @EventHandler
            public void event(EntityDamageEvent e) {e.setCancelled(true);}
        });
        gamePhaseManager.resetTimer();
        broadcast(ChatColor.YELLOW + winner.getName() + " hat gewonnen!");
        Bukkit.getOnlinePlayers().forEach((p) -> {
            p.getInventory().clear();
            p.setHealth(20);
            p.setAbsorptionAmount(0);
            p.getActivePotionEffects().forEach((x) -> p.removePotionEffect(x.getType()));
            p.setFoodLevel(20);
            p.setSaturation(1);
            p.setFireTicks(0);
            p.setGameMode(GameMode.SURVIVAL);
            p.teleport(new Location(BannerManager.lobby, 0.0, 20.0, 0.0));
        });
        startPhase();
    }

    @Override
    public void tick(int tick) {
        int timeLeft = closeTime * 20 - tick;
        if (timeLeft == 0) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Server stoppt.");
            Bukkit.shutdown();
        } else {
            if (timeLeft == 10 * 20 || timeLeft <= 5 * 20) {
                Bukkit.broadcastMessage(ChatColor.GRAY + "Server schließt in " + ChatColor.YELLOW + timeLeft / 20 + "s");
            }
        }
    }

    @Override
    public AbstractGamePhase nextPhase() {
        //TODO("Not yet implemented");
        return null;
    }
}
