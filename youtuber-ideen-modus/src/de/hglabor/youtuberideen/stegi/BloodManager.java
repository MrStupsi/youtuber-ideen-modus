package de.hglabor.youtuberideen.stegi;

import de.hglabor.youtuberideen.YoutuberIdeen;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

public class BloodManager {
    public static Listener damageByEntityEvent = new Listener() {
        @EventHandler
        public void event(EntityDamageByEntityEvent e) {
            if (!(e.getDamager() instanceof Player)) return;
            Player p = (Player) e.getDamager();
            if (p.getInventory().getItemInMainHand().getType().name().endsWith("_SWORD") || p.getInventory().getItemInMainHand().getType().name().endsWith("_AXE")) {
                e.getEntity().getWorld().playEffect(e.getEntity().getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                List<Block> blocks = List.of();
                for (int x = -2; x <= 2; x++) {
                    for (int y = -2; y <= 2; y++) {
                        for (int z = -2; z <= 2; z++) {
                            blocks.add(e.getEntity().getLocation().add(x, y, z).getBlock());
                        }
                    }
                }
                Random r = new Random();
                blocks.stream().filter(
                        (x) -> x.getRelative(BlockFace.UP).getType().isAir() && x.getType().isSolid()
                ).sorted(
                        (x, y) -> r.nextInt(3) - 1
                ).limit(
                        r.nextInt(0, 5)
                ).forEach((x) -> {
                    Block above = x.getRelative(BlockFace.UP);
                    above.setType(Material.REDSTONE_WIRE);
                    above.setMetadata("blood", new FixedMetadataValue(YoutuberIdeen.INSTANCE, e.getEntity().getUniqueId()));
                    Bukkit.getScheduler().runTaskLater(YoutuberIdeen.INSTANCE, () -> {
                        above.removeMetadata("blood", YoutuberIdeen.INSTANCE);
                        if (above.getType() == Material.REDSTONE_WIRE) {
                            above.setType(Material.AIR);
                        }
                        }, 20 * r.nextLong(1, 6));
                });
            }
        }
    };

    public static Listener moveEvent = new Listener() {
        @EventHandler
        public void event(PlayerMoveEvent e) {
            Location to = e.getTo().clone();
            if (to == null) return;
            if (to.getBlock().hasMetadata("blood") && to.getBlock().getType() == Material.REDSTONE_WIRE) {
                UUID uuid = (UUID) to.getBlock().getMetadata("blood").get(0).value();
                if (uuid != e.getPlayer().getUniqueId()) {
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20, 1));
                }
            }
        }
    };
}
