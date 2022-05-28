package de.hglabor.youtuberideen.game.phases;

import de.hglabor.youtuberideen.game.AbstractGamePhase;
import de.hglabor.youtuberideen.game.GamePhaseManager;
import de.hglabor.youtuberideen.game.IGamePhaseManager;
import de.hglabor.youtuberideen.holzkopf.BannerManager;
import de.hglabor.youtuberideen.wichtiger.SkyIslandGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

public class PreInvincibilityPhase extends AbstractGamePhase {
    private int countdown = 5;

    public PreInvincibilityPhase(IGamePhaseManager gamePhaseManager) {
        this.gamePhaseManager = gamePhaseManager;
        gamePhaseManager.resetTimer();
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
            public void event(BlockBreakEvent e) {e.setCancelled(true);}
        });
        listeners.add(new Listener() {
            @EventHandler
            public void event(BlockPlaceEvent e) {e.setCancelled(true);}
        });
        listeners.add(new Listener() {
            @EventHandler
            public void event(EntityDamageEvent e) {e.setCancelled(true);}
        });
        listeners.add(new Listener() {
            @EventHandler
            public void event(FoodLevelChangeEvent e) {
                e.setCancelled(true);
                e.getEntity().setFoodLevel(20);
                e.getEntity().setSaturation(1);
            }
        });
        listeners.add(new Listener() {
            @EventHandler
            public void event(PlayerInteractEvent e) {e.setCancelled(true);}
        });
        listeners.add(new Listener() {
            @EventHandler
            public void event(PlayerDropItemEvent e) {e.setCancelled(true);}
        });
        listeners.add(new Listener() {
            @EventHandler
            public void event(PlayerMoveEvent e) {
                if (e.getPlayer().getWorld() == BannerManager.lobby) {
                    return;
                }
                //Player was just moving mouse
                if (e.getTo().distanceSquared(e.getFrom()) == 0.0) {
                    return;
                }
                Vector spawnLoc = GamePhaseManager.get(e.getPlayer()).spawnLocation;
                if (spawnLoc == null) return;
                Block standingBlock = e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
                if (!standingBlock.getLocation().toVector().equals(spawnLoc)) {
                    Location loc = new Location(
                            SkyIslandGenerator.world,
                            spawnLoc.getX() + 0.5,
                            spawnLoc.getY() + 1,
                            spawnLoc.getZ() + 0.5
                    );
                    loc.setPitch(e.getPlayer().getLocation().getPitch());
                    loc.setYaw(e.getPlayer().getLocation().getYaw());
                    e.getPlayer().teleport(loc);
                }
            }
        });
        startPhase();
    }

    public void tick(int tick) {
        int timeLeft = countdown * 20 - tick;
        if (timeLeft == 0) {
            startNextPhase();
        } else if (tick % 20 == 0) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "Die Runde beginnt in " + ChatColor.YELLOW + timeLeft / 20 + "s");
        }
    }

    public AbstractGamePhase nextPhase() {return new InvincibilityPhase(gamePhaseManager);}
}
