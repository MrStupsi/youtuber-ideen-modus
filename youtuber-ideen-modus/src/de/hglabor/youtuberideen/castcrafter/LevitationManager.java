package de.hglabor.youtuberideen.castcrafter;

import de.hglabor.youtuberideen.YoutuberIdeen;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

public class LevitationManager {
    public static Listener moveEvent = new Listener() {
        @EventHandler
        public void event(PlayerMoveEvent e) {
            Block block = e.getPlayer().getLocation().clone().add(0.0, -1.0, 0.0).getBlock();
            if (block.getType().hasGravity()) {
                BlockData data = block.getBlockData().clone();
                block.setType(Material.AIR);
                FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation().clone().add(0.5, 0.0, 0.5), data);
                fallingBlock.addPassenger(e.getPlayer());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (fallingBlock.getPassengers().isEmpty()) {
                            cancel();
                            return;
                        }
                        fallingBlock.setVelocity(new Vector(0, 1, 0));
                    }
                }.runTaskTimer(YoutuberIdeen.INSTANCE, 0, 1);
            }
        }
    };

    public static Listener dismountEvent = new Listener() {
        @EventHandler
        public void event(EntityDismountEvent e) {
            if (e.getDismounted() instanceof FallingBlock) {
                FallingBlock fallingBlock = (FallingBlock) e.getDismounted();
                fallingBlock.remove();
                Block realBlock = fallingBlock.getLocation().clone().add(0.0, -2.0, 0.0).getBlock();
                realBlock.setType(Material.GRASS_BLOCK, false);
                realBlock.getWorld().playSound(
                        realBlock.getLocation(),
                        Material.GRASS_BLOCK.createBlockData().getSoundGroup().getPlaceSound(),
                        1f,
                        1f
                );
            }
        }
    };
}