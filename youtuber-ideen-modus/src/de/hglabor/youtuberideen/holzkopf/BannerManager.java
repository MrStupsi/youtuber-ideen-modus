package de.hglabor.youtuberideen.holzkopf;

import de.hglabor.youtuberideen.YoutuberIdeen;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class BannerManager {
    public static World lobby = new WorldCreator("world_lobby").type(WorldType.FLAT).createWorld();
    private static Map<UUID, ItemStack> banners = Map.of();
    public static Listener interactEvent = new Listener() {
        @EventHandler
        public void event(PlayerInteractEvent e) {
            ItemStack is = e.getItem();
            Player p = e.getPlayer();
            if (is != null && is.getType() == Material.LOOM) {
                p.openInventory(Bukkit.createInventory(p, InventoryType.LOOM));
            } else if (is != null && is.hasItemMeta() &&
                    is.getItemMeta().hasDisplayName() &&
                    is.getItemMeta().getDisplayName().endsWith("_BANNER")) {
                banners.put(p.getUniqueId(), is);
                addBannerAboveHead(p, is);
            }
        }
    };
    public static Listener joinEvent = new Listener() {
        @EventHandler
        public void event(PlayerJoinEvent e) {
            Player p = e.getPlayer();
            p.getInventory().clear();
            p.getInventory().addItem(new ItemStack(Material.LOOM));
            for (int i = 0; i < 8; i++) {
                p.getInventory().addItem(new ItemStack(Material.WHITE_BANNER, 16));
            }
            Arrays.stream(Material.values()).filter((x) -> x.name().endsWith("_BANNER_PATTERN")).forEach((x) -> {
                p.getInventory().addItem(new ItemStack(x));
            });
            Arrays.stream(Material.values()).filter((x) -> x.name().endsWith("_DYE")).forEach((x) -> {
                p.getInventory().addItem(new ItemStack(x, 64));
            });
            p.teleport(lobby.getSpawnLocation());
            Bukkit.getScheduler().runTaskLater(YoutuberIdeen.INSTANCE, () -> {
                addBannerAboveHead(p, new ItemStack(Material.BLACK_BANNER));
            }, 20);
        }
    };

    public static void addBannerAboveHead(Player p, ItemStack banner) {
        if (banner == null) banner = banners.getOrDefault(p.getUniqueId(), new ItemStack(Material.AIR));
        p.getPassengers().forEach((x) -> x.remove());
        ArmorStand armorStand = (ArmorStand) p.getWorld().spawnEntity(p.getEyeLocation().add(0.0, 0.5, 0.0), EntityType.ARMOR_STAND);
        armorStand.setMarker(true);
        armorStand.setSmall(true);
        armorStand.setVisible(false);
        armorStand.getEquipment().setHelmet(banner);
        p.addPassenger(armorStand);
    }
}
