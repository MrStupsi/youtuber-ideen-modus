package de.hglabor.youtuberideen.game.phases;

import de.hglabor.youtuberideen.bastighg.CakeManager;
import de.hglabor.youtuberideen.castcrafter.LevitationManager;
import de.hglabor.youtuberideen.einfachgustaf.PlayerHider;
import de.hglabor.youtuberideen.game.AbstractGamePhase;
import de.hglabor.youtuberideen.game.IGamePhaseManager;
import de.hglabor.youtuberideen.hugo.BearManager;
import de.hglabor.youtuberideen.sasukey.LavaManager;
import de.hglabor.youtuberideen.seltix.PvPBotManager;
import de.hglabor.youtuberideen.stegi.BloodManager;
import de.hglabor.youtuberideen.wichtiger.SkyIslandGenerator;
import net.minecraft.util.Unit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PvPPhase extends AbstractGamePhase {
    private Player winner = null;

    public PvPPhase(IGamePhaseManager gamePhaseManager) {
        this.gamePhaseManager = gamePhaseManager;
        listeners.add(LevitationManager.moveEvent);
        listeners.add(LevitationManager.dismountEvent);
        listeners.add(BloodManager.moveEvent);
        listeners.add(BloodManager.damageByEntityEvent);
        listeners.add(CakeManager.blockBreakEvent);
        listeners.add(CakeManager.winEvent((p) -> {
            winner = p;
            startNextPhase();
            return Unit.a;
        }));
        listeners.add(PlayerHider.playerMoveEvent);
        listeners.add(BearManager.flameEvent);
        listeners.add(LavaManager.interactEvent);
        listeners.add(PvPBotManager.entityDamageByEntityEvent);
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
            public void event(PlayerDeathEvent e) {
                if (e.getEntity().getGameMode() == GameMode.SURVIVAL) {
                    e.getDrops().addAll(CakeManager.getRandomCakeIngridients());
                    e.getDrops().forEach((item) -> e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), item));
                    Bukkit.broadcastMessage(ChatColor.YELLOW + e.getDeathMessage());
                    e.setDeathMessage(null);
                    e.getEntity().getPassengers().clear();
                    e.getEntity().setGameMode(GameMode.SPECTATOR);
                    Location loc = e.getEntity().getKiller() == null ? null : e.getEntity().getKiller().getLocation();
                    e.getEntity().teleport(loc != null ? loc : new Location(SkyIslandGenerator.world, 0.0, 150.0, 0.0));
                }
            }
        });
        startPhase();
    }

    @Override
    public void tick(int tick) {
        if (tick % (2 * 20) == 0 && LavaManager.currentLavaLevel < 200) {
            LavaManager.riseLava(1);
            if (LavaManager.currentLavaLevel % 10 == 0) {
                Bukkit.broadcastMessage(ChatColor.GRAY + "Die Lava ist auf Höhe " + ChatColor.YELLOW + LavaManager.currentLavaLevel);
            }
        }
    }

    @Override
    public AbstractGamePhase nextPhase() {return new EndPhase(gamePhaseManager, winner);}
}
