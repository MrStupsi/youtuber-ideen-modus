package de.hglabor.youtuberideen.seltix;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.item.ItemAxe;
import net.minecraft.world.item.ItemCooldown;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftZombie;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class PvPBot extends EntityZombie {
    public ItemCooldown cooldowns = new ItemCooldown();
    public boolean isSprintJumping = false;

    public PvPBot(World w, String name) {
        super(EntityTypes.be, ((CraftWorld) w).getHandle());
        bQ = new PathfinderGoalSelector(s.ac());
        bR = new PathfinderGoalSelector(s.ac());
        u();
        a(EnumItemSlot.b, new ItemStack(Items.sv));
        a(EnumItemSlot.a, new ItemStack(Items.mC));
        DisguiseAPI.disguiseToAll(getBukkitEntity(), new PlayerDisguise(name, name));
    }

    @Override
    public void u() {
        bQ.a(2, new StrafeAttackGoal(this, 1.0, 20, 3.0f));
        bR.a(2, new PathfinderGoalNearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
    }

    @Override
    public void k() {
        super.k();
        cooldowns.a();
    }

    @Override
    public void eA() {
        super.eA();
    }

    @Override
    public void g(boolean sprinting) {
        super.g(sprinting);
        a(GenericAttributes.d).a(sprinting ? 0.4 : 0.2);
    }

    @Override
    public void d(EntityLiving attacker) {
        if (attacker.es().c() instanceof ItemAxe) {
            disableShield(true);
        }
    }

    private void disableShield(boolean sprinting) {
        float f = 0.25f + (float) EnchantmentManager.f(this) * 0.05f;
        if (sprinting) f += 0.75f;
        if (R.nextFloat() < f) {
            this.cooldowns.a(Items.sv, 100);
            eS();
            s.a(this, (byte) 30);
        }
    }

    @Override
    public void ee() {}
    @Override
    public boolean dJ() {return false;}
    @Override
    public boolean K_() {return false;}
    @Override
    public SoundEffect aF() {return SoundEffects.oP;}
    @Override
    public SoundEffect aG() {return SoundEffects.oN;}
    @Override
    public SoundEffect aH() {return SoundEffects.oO;}
    @Override
    public SoundCategory cO() {return SoundCategory.h;}
    @Override
    public SoundEffect x_() {return SoundEffects.oF;}
    @Override
    public EntityLiving.a eg() {return new EntityLiving.a(SoundEffects.oM, SoundEffects.oC);}
    @Override
    public SoundEffect c(DamageSource source) {
        return source == DamageSource.c ? SoundEffects.oJ : source == DamageSource.h ? SoundEffects.oH : source == DamageSource.u ? SoundEffects.oK : source == DamageSource.v ? SoundEffects.oI : SoundEffects.oG;
    }

    public boolean isReadyToStrafe() {
        return true;
    }

    public PvPBot spawnAt(Location location) {
        PvPBotManager.botIds.add(aj);
        s.b(this);
        getBukkitEntity().teleport(location);
        return this;
    }
}
