package de.hglabor.youtuberideen.game.phases;

import com.google.common.collect.Lists;
import de.hglabor.youtuberideen.Shuffler;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpawnPhase extends AbstractGamePhase {
    private List<UUID> toTeleport = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).toList();
    private int toTeleportAmount = toTeleport.size();

    public SpawnPhase(IGamePhaseManager gamePhaseManager) {
        this.gamePhaseManager = gamePhaseManager;
        List<Vector> spawns = SkyIslandGenerator.spawnLocations.stream().sorted(new Shuffler<>()).filter(
            (x) -> {
                return SkyIslandGenerator.world.getWorldBorder().isInside(x.toLocation(SkyIslandGenerator.world));
            }
        ).limit(toTeleportAmount).toList();
        for (int i = 0; i < toTeleport.size(); i++) {
            Player p = Bukkit.getPlayer(toTeleport.stream().toList().get(i));
            if (p != null) {
                GamePhaseManager.get(p).spawnLocation = spawns.get(i);
            }
        }
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
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Teleportiere Spieler");
        startPhase();
    }

    @Override
    public void tick(int tick) {
        System.out.println("tick");
        if (toTeleport.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Alle Spieler teleportiert");
            startNextPhase();
            return;
        }
        List<UUID> teleported = Lists.newArrayList();
        System.out.println(toTeleport.size());
        toTeleport.stream().limit(6).forEach((uuid) -> {
            teleported.add(uuid);
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                GamePhaseManager.User user = GamePhaseManager.get(p);
                Vector vec = user.spawnLocation;
                if (vec != null) {
                    Location loc = vec.toLocation(SkyIslandGenerator.world);
                    if (loc == null) return;
                    p.getPassengers().forEach(p::removePassenger);
                    //OMG CANT TELEPORT WITH PASSENGERS BÖÖÖ
                    p.teleport(loc.add(0.5, 1.0, 0.5));
                    p.getInventory().clear();
                    p.setHealth(20);
                    p.setAbsorptionAmount(0);
                    p.getActivePotionEffects().forEach((x) -> p.removePotionEffect(x.getType()));
                    p.setFoodLevel(20);
                    p.setSaturation(1);
                    p.setFireTicks(0);
                    p.setGameMode(GameMode.SURVIVAL);
                }
            }
        });
        toTeleport.removeAll(teleported);
        Bukkit.broadcastMessage(ChatColor.YELLOW + "" + (toTeleportAmount - toTeleport.size()) + ChatColor.GRAY + "/" + ChatColor.YELLOW + toTeleportAmount + ChatColor.GRAY + " Spieler teleportiert");
    }

    @Override
    public AbstractGamePhase nextPhase() {return new PreInvincibilityPhase(gamePhaseManager);}
}