package de.hglabor.youtuberideen.einfachgustaf;

import de.hglabor.youtuberideen.YoutuberIdeen;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class PlayerHider {
    private static Map<UUID, Location> prevPositions = Map.of();
    private static Map<UUID, Integer> afkCounter = Map.of();
    private static Map<UUID, FakeBlock> fakeBlocks = Map.of();
    private static final int AFK_TIME = 4;

    public PlayerHider() {
        Bukkit.getScheduler().runTaskTimer(YoutuberIdeen.INSTANCE, () -> {
            Bukkit.getOnlinePlayers().stream().filter(
                    (x) -> x.getGameMode() == GameMode.SURVIVAL
            ).forEach((x) -> {
                ItemStack item = x.getInventory().getItemInMainHand();
                UUID uuid = x.getUniqueId();
                Location blockLoc = new Location(x.getWorld(), x.getLocation().getBlockX(), x.getLocation().getBlockY(), x.getLocation().getBlockZ());
                if (!prevPositions.get(uuid).equals(blockLoc) || (item == null || !item.getType().isBlock() || item.getType().isAir())) {
                    prevPositions.put(uuid, blockLoc);
                    afkCounter.put(uuid, 0);
                } else {
                    int counter = afkCounter.computeIfAbsent(uuid, (y) -> 0);
                    afkCounter.put(uuid, Math.min(AFK_TIME, counter + 1));
                }
                hideAsBlock(x, afkCounter.get(uuid));
            });
        }, 0, 20);
    }

    public static Listener playerMoveEvent = new Listener() {
        @EventHandler
        public void event(PlayerMoveEvent e) {
            FakeBlock fakeBlock = fakeBlocks.get(e.getPlayer().getUniqueId());
            if (fakeBlock == null) return;
            Location x = e.getTo();
            Location blockLoc = new Location(x.getWorld(), x.getBlockX(), x.getBlockY(), x.getBlockZ());
            if (blockLoc != prevPositions.get(e.getPlayer().getUniqueId())) {
                fakeBlock.cF().forEach((entity) -> entity.ah());
                fakeBlock.ah();
                e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), fakeBlock.blockData.getSoundGroup().getBreakSound(), 1f, 1f);
                e.getPlayer().getWorld().playEffect(
                        blockLoc.add(0.0, 0.5, 0.0),
                        Effect.STEP_SOUND,
                        fakeBlock.blockData.getMaterial()
                );
                fakeBlocks.remove(e.getPlayer().getUniqueId());
                Packet<?> packet = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) e.getPlayer()).getHandle());
                Bukkit.getOnlinePlayers().forEach(t -> ((CraftPlayer) t).getHandle().b.a(packet));
                e.getPlayer().sendMessage(ChatColor.YELLOW + "Sichtbar!");
            }
        }
    };

    private void hideAsBlock(Player p, int afkTime) {
        ItemStack item = p.getInventory().getItemInMainHand();
        if (!item.getType().isBlock()) return;
        if (item.getType().isAir()) return;
        if (afkTime == AFK_TIME) {
            if (!fakeBlocks.containsKey(p.getUniqueId())) {
                Location blockLoc = new Location(p.getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ());
                fakeBlocks.put(
                    p.getUniqueId(),
                    new FakeBlock(((CraftPlayer) p).getWorld(), item.getType().createBlockData()).spawnAt(blockLoc.add(0.5, 0.0, 0.5))
                );
                p.playSound(p.getLocation(), item.getType().createBlockData().getSoundGroup().getPlaceSound(), 1f, 1f);
                p.sendMessage(ChatColor.YELLOW + "Getarnt!");
                Packet<?> packet = new PacketPlayOutEntityDestroy(p.getEntityId());
                Bukkit.getOnlinePlayers().forEach(t -> ((CraftPlayer) t).getHandle().b.a(packet));
            }
        } else if (afkTime > 0) {
            p.sendMessage(ChatColor.GRAY + "Du musst noch " + ChatColor.YELLOW + (AFK_TIME - afkTime) + "s " + ChatColor.GRAY + " stehenbleiben, um dich als Block zu tarnen");
        }
    }
}
