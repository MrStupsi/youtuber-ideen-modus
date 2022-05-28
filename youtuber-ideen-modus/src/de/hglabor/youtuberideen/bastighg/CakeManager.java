package de.hglabor.youtuberideen.bastighg;

import de.hglabor.youtuberideen.YoutuberIdeen;
import de.hglabor.youtuberideen.game.mechanic.LootTables;
import net.minecraft.util.Unit;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class CakeManager {
    public static Listener winEvent(Function<Player, Unit> callBack) {
        return new Listener() {
            @EventHandler
            public void event(BlockPlaceEvent e) {
                Block block = e.getBlock();
                if (block.getType() != Material.CAKE) return;
                if (block.getRelative(BlockFace.DOWN, 1).getType() != Material.CAKE) return;
                if (block.getRelative(BlockFace.DOWN, 2).getType() != Material.CAKE) return;
                celebrateWinner(block.getLocation());
                callBack.apply(e.getPlayer());
            }
        };
    }

    public static Listener blockBreakEvent = new Listener() {
        @EventHandler
        public void event(BlockBreakEvent e) {
            Block block = e.getBlock();
            if (block.getType() == Material.CAKE) {
                getRandomCakeIngridients().forEach((x) -> {
                    block.getWorld().dropItem(block.getLocation().add(0.5, 0.0, 0.5), x);
                });
            }
        }
    };

    public static List<ItemStack> getRandomCakeIngridients() {
        Random r = new Random();
        return LootTables.cakeIngridients.stream().sorted((x, y) -> r.nextInt(3) - 1).limit(r.nextInt(0, LootTables.cakeIngridients.size())).toList();
    }

    private static void celebrateWinner(Location location) {
        Runnable runnable = () -> {
            Firework fireWork = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            FireworkMeta meta = fireWork.getFireworkMeta();
            meta.setPower(2);
            meta.addEffect(FireworkEffect.builder().withTrail().withColor(Color.AQUA).flicker(true).build());
            fireWork.setFireworkMeta(meta);
            Bukkit.getScheduler().runTaskLater(YoutuberIdeen.INSTANCE, () -> fireWork.detonate(), 20);
        };
        Bukkit.getScheduler().runTaskLater(YoutuberIdeen.INSTANCE, runnable, 5);
        Bukkit.getScheduler().runTaskLater(YoutuberIdeen.INSTANCE, runnable, 10);
        Bukkit.getScheduler().runTaskLater(YoutuberIdeen.INSTANCE, runnable, 15);
    }
}
