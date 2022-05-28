package de.hglabor.youtuberideen.einfachgustaf;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.block.data.CraftBlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;

public final class FakeBlock extends EntityArmorStand {
    private World world;
    public BlockData blockData;

    public FakeBlock(World world, BlockData blockData) {
        super(EntityTypes.c, ((CraftWorld) world).getHandle());
        this.world = world;
        this.blockData = blockData;
        t(true);
        e(true);
        a(true);
        d(true);
        m(true);
        j(true);
    }

    public FakeBlock spawnAt(Location location) {
        s.b(this);
        ArmorStand as = (ArmorStand) getBukkitEntity();
        as.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 10, false, false));
        as.teleport(location);
        CustomFallingBlock fallingBlock = new CustomFallingBlock(s.getWorld().getHandle(), as.getLocation(), ((CraftBlockData) blockData).getState());
        s.addFreshEntity(fallingBlock, CreatureSpawnEvent.SpawnReason.CUSTOM);
        fallingBlock.a(this, true);
        return this;
    }

    private class CustomFallingBlock extends EntityFallingBlock {
        public CustomFallingBlock(WorldServer level, Location loc, IBlockData state) {
            super(EntityTypes.C, level);
            this.q = true;
            this.e(loc.getX(), loc.getY(), loc.getZ());
            this.g(Vec3D.a);
            this.t = loc.getX();
            this.u = loc.getY();
            this.v = loc.getZ();
            this.a(this.cW());
            try {
                Field f = EntityFallingBlock.class.getDeclaredField("ao");
                f.setAccessible(true);
                f.set(this, state);
            } catch (Exception e) {}
            c = false;
            m(true);
            b = 1;
            aq = false;
            j(true);
        }

        @Override
        public void k() {}
    }
}