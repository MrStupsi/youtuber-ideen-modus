package de.hglabor.youtuberideen.sasukey;

import de.hglabor.youtuberideen.wichtiger.SkyIslandGenerator;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class LavaManager {
    public static int defaultLavaLevel = SkyIslandGenerator.MIN_Y_ISLAND / 2;
    public static int currentLavaLevel = defaultLavaLevel;

    public static Listener interactEvent = new Listener() {
        @EventHandler
        public void event(PlayerInteractEvent e) {
            Player p = e.getPlayer();
            ItemStack is = e.getItem();
            Action a = e.getAction();
            if (is != null && is.getType() != Material.LILY_PAD) return;
            if (a != Action.RIGHT_CLICK_AIR && a != Action.RIGHT_CLICK_BLOCK) return;
            Block targetBlock = p.getTargetBlockExact(5, FluidCollisionMode.SOURCE_ONLY);
            if (targetBlock != null && targetBlock.getType() == Material.LAVA) {
                Block spaceAbove = targetBlock.getRelative(BlockFace.UP);
                spaceAbove.setType(Material.LILY_PAD, false);
                spaceAbove.getWorld().playSound(
                        spaceAbove.getLocation(),
                        Material.LILY_PAD.createBlockData().getSoundGroup().getPlaceSound(),
                        1f,
                        1f
                );
            }
        }
    };

    public static void riseLava(int howMuch) {
        int size = (int) (SkyIslandGenerator.world.getWorldBorder().getSize() / 2);
        for (int x = -size; x <= size; x++) {
            for (int z = -size; z <= size; z++) {
                for (int i = 0; i <= howMuch; i++) {
                    int y = currentLavaLevel + i;
                    BlockState state = SkyIslandGenerator.world.getBlockState(x, y, z);
                    if (state.getType() == Material.LAVA) continue;
                    if (state.getType().isAir()) {
                        SkyIslandGenerator.placeableBlocks.add(
                            new SkyIslandGenerator.PlaceableBlock(
                                SkyIslandGenerator.world,
                                new Vector(x, y, z),
                                Blocks.B.n(),
                                2
                            )
                        );
                    }
                    currentLavaLevel++;
                }
            }
        }
        currentLavaLevel += howMuch;
    }
}
