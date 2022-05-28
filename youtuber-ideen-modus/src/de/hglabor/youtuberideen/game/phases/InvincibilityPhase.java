package de.hglabor.youtuberideen.game.phases;

import de.hglabor.youtuberideen.YoutuberIdeen;
import de.hglabor.youtuberideen.bastighg.CakeManager;
import de.hglabor.youtuberideen.einfachgustaf.PlayerHider;
import de.hglabor.youtuberideen.game.AbstractGamePhase;
import de.hglabor.youtuberideen.game.IGamePhaseManager;
import de.hglabor.youtuberideen.holzkopf.BannerManager;
import de.hglabor.youtuberideen.hugo.BearManager;
import de.hglabor.youtuberideen.sasukey.LavaManager;
import de.hglabor.youtuberideen.seltix.PvPBot;
import de.hglabor.youtuberideen.veto.FlyingIronGolem;
import de.hglabor.youtuberideen.wichtiger.SkyIslandGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Random;

public class InvincibilityPhase extends AbstractGamePhase {
    private int invincibilityAmount = 15;

    public InvincibilityPhase(IGamePhaseManager gamePhaseManager) {
        this.gamePhaseManager = gamePhaseManager;
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Die Schutzzeit beginnt!");
        gamePhaseManager.resetTimer();
        new PlayerHider();
        listeners.add(PlayerHider.playerMoveEvent);
        listeners.add(BearManager.flameEvent);
        listeners.add(LavaManager.interactEvent);
        listeners.add(CakeManager.blockBreakEvent);
        // listeners += ChestLoot.playerOpenChestEvent()
        listeners.add(new Listener() {
            @EventHandler
            public void event(EntityDamageEvent e) {e.setCancelled(true);}
        });
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
        Bukkit.getScheduler().runTaskLater(YoutuberIdeen.INSTANCE, () -> Bukkit.getOnlinePlayers().forEach((x) -> BannerManager.addBannerAboveHead(x, null)), 3);
        List<EntityType> toSpawn = List.of(EntityType.IRON_GOLEM, EntityType.PANDA, EntityType.POLAR_BEAR, EntityType.ZOMBIE);
        List<String> youtuber = List.of(
            "NoRiskk",
            "BastiGHG",
            "Sasukey",
            "Wichtiger",
            "ZweifachGustaf",
            "NQRMAN",
            "Stxgi",
            "Seltix",
            "Wichtiger",
            "LetsHugo"
        );
        Random r = new Random();
        SkyIslandGenerator.spawnLocations.forEach((x) -> {
            EntityType type = toSpawn.get(r.nextInt(toSpawn.size()));
            Location loc = x.toLocation(SkyIslandGenerator.world).add(0.5, 1.0, 0.5);
            switch (type) {
                case IRON_GOLEM: new FlyingIronGolem(SkyIslandGenerator.world).spawnAt(loc);
                case ZOMBIE: new PvPBot(SkyIslandGenerator.world, youtuber.get(r.nextInt(youtuber.size()))).spawnAt(loc);
                default: SkyIslandGenerator.world.spawnEntity(loc, type);
            }
        });
        startPhase();
    }

    @Override
    public void tick(int tick) {
        int timeLeft = invincibilityAmount * 20 - tick;
        if (tick == invincibilityAmount * 20) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Die Schutzzeit ist vorbei!");
            startNextPhase();
        } else if (timeLeft % (5 * 20) == 0 || timeLeft <= (5 * 20)) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "Die Schutzzeit endet in " + ChatColor.YELLOW + timeLeft / 20 + "s");
        }
    }

    @Override
    public AbstractGamePhase nextPhase() {return new PvPPhase(gamePhaseManager);}
}
