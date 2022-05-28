package de.hglabor.youtuberideen.hugo;

import de.hglabor.youtuberideen.YoutuberIdeen;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.Random;

public class BearManager {
    public static Listener joinEvent = new Listener() {
        @EventHandler
        public void event(PlayerJoinEvent e) {
            //Purpur hahahahah YOOOO
            Player p = e.getPlayer();
            p.addAttachment(YoutuberIdeen.INSTANCE, "allow.ride.polar_bear", true);
            p.addAttachment(YoutuberIdeen.INSTANCE, "allow.ride.panda", true);
        }
    };
    public static Listener flameEvent = new Listener() {
        @EventHandler
        public void event(PlayerInteractEvent e) {
            //TODO add cooldown...
            Player p = e.getPlayer();
            if (p.isInsideVehicle() && (
                    p.getVehicle().getType() == EntityType.POLAR_BEAR ||
                    p.getVehicle().getType() == EntityType.PANDA)) {
                shootFlames(p);
            }
        }
    };

    private static void shootFlames(LivingEntity entity) {
        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                World world = entity.getWorld();
                Location loc = entity.getLocation();
                Location eyeloc = entity.getEyeLocation();
                Vector playerDirection = eyeloc.getDirection();
                Vector particleVector = playerDirection.clone();
                playerDirection.multiply(8);
                double x = particleVector.getX();
                particleVector.setX(-particleVector.getZ());
                particleVector.setZ(x);
                particleVector.divide(new Vector(3, 3, 3));
                Location particleLocation = particleVector.toLocation(world).add(eyeloc);
                world.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 0.6f, 0.65f + new Random().nextFloat() * 0.4f);
                for (int i = 0; i < 10; i++){
                    shootSingleFlame(entity, playerDirection, particleLocation);
                }
                new BlockIterator(entity, 10).forEachRemaining((a) -> {
                    world.getNearbyEntities(a.getLocation().add(0.5, 0.5, 0.5), 3.0, 3.0, 3.0).stream().filter((b) -> {
                        return b instanceof LivingEntity && entity.hasLineOfSight(b) &&
                               b != entity && b != entity.getVehicle();
                    }).forEach((y) -> {
                        y.setFireTicks(40);
                    });
                });
                i++;
                if (i >= 10) cancel();
            }
        }.runTaskTimer(YoutuberIdeen.INSTANCE, 0, 1);
    }

    private static void shootSingleFlame(LivingEntity entity, Vector playerDirection, Location particleLocation) {
        World world = entity.getWorld();
        Vector particlePath = playerDirection.clone();
        particlePath.add(
                new Vector(
                        Math.random() - Math.random(),
                        Math.random() - Math.random(),
                        Math.random() - Math.random()
                )
        );
        Location offsetLocation = particlePath.toLocation(world);
        world.spawnParticle(
                Particle.FLAME,
                particleLocation,
                0,
                offsetLocation.getX(),
                offsetLocation.getY(),
                offsetLocation.getZ(),
                0.1
        );
    }
}
