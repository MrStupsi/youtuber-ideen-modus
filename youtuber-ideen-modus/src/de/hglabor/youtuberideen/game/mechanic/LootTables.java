package de.hglabor.youtuberideen.game.mechanic;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.List;
import java.util.Set;

// ja das ist scheiße könnte man geiler machen aber der spielmodus wird eh direkt verworfen
public class LootTables {
    //hmm lecker
    public static List<ItemStack> cakeIngridients = Lists.newArrayList(
            new ItemStack(Material.MILK_BUCKET),
            new ItemStack(Material.MILK_BUCKET),
            new ItemStack(Material.MILK_BUCKET),
            new ItemStack(Material.SUGAR),
            new ItemStack(Material.EGG),
            new ItemStack(Material.SUGAR),
            new ItemStack(Material.WHEAT),
            new ItemStack(Material.WHEAT),
            new ItemStack(Material.WHEAT)
    );

    public static Set<LootItem> normal = Sets.newHashSet(
            new LootItem(new ItemStack(Material.CAKE), 1, 1, 1),
            new LootItem(new ItemStack(Material.DIAMOND_HELMET), 1, 1, 15),
            new LootItem(new ItemStack(Material.DIAMOND_CHESTPLATE), 1, 1, 15),
            new LootItem(new ItemStack(Material.DIAMOND_LEGGINGS), 1, 1, 15),
            new LootItem(new ItemStack(Material.DIAMOND_BOOTS), 1, 1, 15),
            new LootItem(new ItemStack(Material.WATER_BUCKET), 1, 1, 15),
            new LootItem(new ItemStack(Material.LAVA_BUCKET), 1, 1, 15),
            new LootItem(new ItemStack(Material.MILK_BUCKET), 1, 1, 45),
            new LootItem(new ItemStack(Material.NETHERITE_HELMET), 1, 1, 5),
            new LootItem(new ItemStack(Material.NETHERITE_CHESTPLATE), 1, 1, 5),
            new LootItem(new ItemStack(Material.NETHERITE_LEGGINGS), 1, 1, 5),
            new LootItem(new ItemStack(Material.NETHERITE_BOOTS), 1, 1, 5),
            new LootItem(new ItemStack(Material.NETHERITE_SWORD), 1, 1, 5),
            new LootItem(new ItemStack(Material.DIAMOND_SWORD), 1, 1, 10),
            new LootItem(new ItemStack(Material.IRON_PICKAXE), 1, 1, 20),
            new LootItem(new ItemStack(Material.CROSSBOW), 1, 1, 10),
            new LootItem(new ItemStack(Material.EGG), 1, 15, 30),
            new LootItem(new ItemStack(Material.SUGAR), 1, 4, 30),
            new LootItem(new ItemStack(Material.HAY_BLOCK), 1, 1, 20),
            new LootItem(new ItemStack(Material.SHIELD), 1, 1, 25),
            new LootItem(new ItemStack(Material.OAK_PLANKS), 1, 64, 80),
            new LootItem(new ItemStack(Material.COBBLESTONE), 1, 64, 80),
            new LootItem(new ItemStack(Material.STONE), 1, 64, 80),
            new LootItem(new ItemStack(Material.LILY_PAD), 1, 64, 35),
            new LootItem(new ItemStack(Material.PUMPKIN_PIE), 1, 16, 50),
            new LootItem(new ItemStack(Material.COOKED_BEEF), 1, 16, 50),
            new LootItem(new ItemStack(Material.APPLE), 1, 5, 30),
            new LootItem(new ItemStack(Material.GOLDEN_APPLE), 1, 1, 30),
            new LootItem(new ItemStack(Material.SCAFFOLDING), 1, 16, 45),
            new LootItem(new ItemStack(Material.TNT), 1, 16, 20),
            new LootItem(new ItemStack(Material.ARROW), 1, 16, 25),
            new LootItem(new ItemStack(Material.COBWEB), 1, 3, 13),
            new LootItem(new ItemStack(Material.IRON_INGOT), 1, 7, 20),
            new LootItem(new ItemStack(Material.DIAMOND), 1, 7, 10),
            new LootItem(new ItemStack(Material.ENDER_PEARL), 1, 2, 15),
            new LootItem(new ItemStack(Material.COBWEB), 1, 3, 13),
            new LootItem(new ItemStack(Material.FLINT_AND_STEEL), 1, 1, 10)
    );

            /*new LootItem(new ItemStack(Material.POTION) {

        meta<PotionMeta> {
            this.basePotionData = PotionData(PotionType.FIRE_RESISTANCE)
            this.color = Color.ORANGE
        }
    }, 15),
    LootItem(itemStack(Material.SPLASH_POTION) {
        meta<PotionMeta> {
            this.basePotionData = PotionData(PotionType.INSTANT_HEAL)
            this.color = Color.RED
        }
    }, 5, 15),
    LootItem(itemStack(Material.POTION) {
        meta<PotionMeta> {
            this.basePotionData = PotionData(PotionType.REGEN)
            this.color = Color.PURPLE
        }
    }, 5, 10),
            )*/

    public static class LootItem {
        public ItemStack itemStack;
        public int minAmount;
        public int maxAmount;
        public int probability;

        public LootItem(ItemStack itemStack, int minAmount, int maxAmount, int probability) {
            this.itemStack = itemStack;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.probability = probability;
        }
    }
}
