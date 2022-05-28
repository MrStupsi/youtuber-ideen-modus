package de.hglabor.youtuberideen.seltix;

import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class PvPBotManager {
    public static Set<UUID> botIds = new HashSet<>();

    public static Listener entityDamageByEntityEvent = new Listener() {
        @EventHandler
        public void event(EntityDamageByEntityEvent e) {
            if (!botIds.contains(e.getEntity().getUniqueId())) return;
            if (!(e.getDamager() instanceof Player)) return;
            Player attacker = (Player) e.getDamager();
            PvPBot bot = (PvPBot) ((CraftEntity) e.getEntity()).getHandle();
            if (bot.eT()) {
                attacker.playSound(
                        attacker.getLocation(),
                        Sound.ITEM_SHIELD_BLOCK,
                        1.0f,
                        0.8f + new Random().nextFloat() * 0.4f
                );
                bot.d(((CraftPlayer) attacker).getHandle());
                e.setCancelled(true);
            }
        }
    };
}
