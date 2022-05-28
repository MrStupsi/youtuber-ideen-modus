package de.hglabor.youtuberideen.game.phases;

import de.hglabor.youtuberideen.YoutuberIdeen;
import de.hglabor.youtuberideen.bastighg.SkinChanger;
import de.hglabor.youtuberideen.game.AbstractGamePhase;
import de.hglabor.youtuberideen.game.IGamePhaseManager;
import de.hglabor.youtuberideen.holzkopf.BannerManager;
import de.hglabor.youtuberideen.hugo.BearManager;
import de.hglabor.youtuberideen.veto.GolemManager;
import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LobbyPhase extends AbstractGamePhase {
    private World lobby = new WorldCreator("world_lobby").type(WorldType.FLAT).createWorld();
    
    public LobbyPhase(IGamePhaseManager gamePhaseManager) {
        this.gamePhaseManager = gamePhaseManager;
        lobby.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        lobby.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        lobby.setGameRule(GameRule.DISABLE_RAIDS, true);
        lobby.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        lobby.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        lobby.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        lobby.getWorldBorder().setSize(30 * 2.0);
        Bukkit.getWorlds().forEach((x) -> {
            x.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
            x.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            x.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        });
        listeners.add(SkinChanger.playerJoinEvent);
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
        listeners.add(BannerManager.joinEvent);
        listeners.add(BannerManager.interactEvent);
        listeners.add(BearManager.joinEvent);
        listeners.add(GolemManager.joinEvent);
        listeners.add(new Listener() {
            @EventHandler
            public void event(PlayerJoinEvent e) {
                e.setJoinMessage(null);
                broadcast(ChatColor.GREEN + e.getPlayer().getName() + "hat das Spiel betreten.");
            }
        });
        listeners.add(new Listener() {
            @EventHandler
            public void event(PlayerQuitEvent e) {
                e.setQuitMessage(null);
                broadcast(ChatColor.RED + e.getPlayer().getName() + "hat das Spiel verlassen.");
            }
        });
        PluginCommand cmd = YoutuberIdeen.INSTANCE.getCommand("start");
        cmd.setExecutor((sender, cmd2, args, label) -> {
            if (Bukkit.getOnlinePlayers().size() >= 2) {
                broadcast("starte haha norisk");
                startNextPhase();
            } else {
                sender.sendMessage("§cDas Spiel kann nicht gestartet werden.");
            }
            return true;
        });
        cmd = YoutuberIdeen.INSTANCE.getCommand("forcestart");
        cmd.setPermission("hglabor.admin");
        cmd.setExecutor((sender, cmd2, args, label) -> {
            broadcast("starte haha norisk");
            startNextPhase();
            return true;
        });
        startPhase();
    }

    @Override
    public void tick(int tick) {
        gamePhaseManager.resetTimer();
    }

    @Override
    public AbstractGamePhase nextPhase() {return new SpawnPhase(gamePhaseManager);}
}
