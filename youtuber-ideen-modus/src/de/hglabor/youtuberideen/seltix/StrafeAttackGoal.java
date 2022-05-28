package de.hglabor.youtuberideen.seltix;

import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.item.Items;
import org.bukkit.entity.LivingEntity;

import java.util.EnumSet;
import java.util.Random;

public class StrafeAttackGoal extends PathfinderGoal {
    private PvPBot bot;
    private double speedModifier;
    private int attackIntervalMin;
    private float range;
    private float attackRadiusSqr;
    private int attackTime = -1;
    private int seeTime = 0;
    private boolean strafingClockwise = false;
    private boolean strafingBackwards = false;
    private int strafingTime = -1;
    private int ticksUntilNextAttack = 0;

    public void setMinAttackInterval(int attackInterval) {
        attackIntervalMin = attackInterval;
    }

    public StrafeAttackGoal(PvPBot bot, double speedModifier, int attackIntervalMin, float range) {
        this.bot = bot;
        this.speedModifier = speedModifier;
        this.attackIntervalMin = attackIntervalMin;
        this.range = range;
        this.attackRadiusSqr = range * range;;
        a(EnumSet.of(Type.a, Type.b, Type.c));
    }

    @Override
    public boolean a() {
        return bot.G() != null && isReadyToStrafe();
    }

    private boolean isReadyToStrafe() {return bot.isReadyToStrafe();}

    @Override
    public boolean b() {
        return (a() || !bot.D().l()) && isReadyToStrafe();
    }

    @Override
    public void c() {
        super.c();
        bot.u(true);
        ticksUntilNextAttack = 0;
    }

    @Override
    public void d() {
        super.d();
        bot.u(false);
        seeTime = 0;
        attackTime = -1;
        bot.eS();
    }

    @Override
    public boolean E_() {
        return true;
    }

    @Override
    public void e() {
        EntityLiving livingEntity = bot.G();
        if (livingEntity != null) {
            ticksUntilNextAttack = Math.max(0, ticksUntilNextAttack - 1);
            if (bot.isSprintJumping) {
                bot.C().a();
            }
            if (this.isTimeToAttack()) {
                this.attackRadiusSqr = range * range;
            }
            double distance = bot.h(livingEntity.dc(), livingEntity.de(), livingEntity.di());
            boolean canSeeEntity = bot.E().a(livingEntity);
            boolean seeTimeBiggerZero = seeTime > 0;
            if (canSeeEntity != seeTimeBiggerZero) {
                seeTime = 0;
            }
            if (canSeeEntity) {
                ++seeTime;
            } else {
                --seeTime;
            }
            if (distance <= (double) attackRadiusSqr && seeTime >= 20) {
                bot.D().n();
                ++strafingTime;
            } else {
                bot.D().a(livingEntity, speedModifier);
                strafingTime = -1;
            }
            if (strafingTime >= 20) {
                    if (bot.dL().nextFloat() < 0.3) {
                        strafingClockwise = !strafingClockwise;
                    }
                    if (bot.dL().nextFloat() < 0.3) {
                        strafingBackwards = !strafingBackwards;
                    }
                    strafingTime = 0;
                }
                if (strafingTime > -1) {
                    if (distance > attackRadiusSqr * 0.75f) {
                        strafingBackwards = false;
                    } else if (distance < attackRadiusSqr * 0.25f) {
                        strafingBackwards = true;
                    }
                    bot.g(!strafingBackwards);
                    bot.isSprintJumping = bot.bO() && new Random().nextBoolean();
                    bot.A().a(strafingBackwards ? -0.5f : 0.5f, strafingClockwise ? 0.5f : -0.5f);
                    bot.a(livingEntity, 180f, 180f);
                } else {
                    bot.a(livingEntity, 180f, 180f);
                }
                if (distance <= attackRadiusSqr && seeTime >= 20) {
                    checkAndPerformAttack(livingEntity);
                }
                if (bot.eM()) {
                    if (!canSeeEntity && seeTime < -60) {
                        bot.eS();
                    } else if (canSeeEntity) {
                        int i = bot.eQ();
                        if (i >= 20) {
                            bot.eS();
                            //this.bot.performRangedAttack(livingEntity, BowItem.getPowerForTime(i));
                            attackTime = attackIntervalMin;
                        }
                    }
                } else if (--attackTime <= 0 && seeTime >= -60) {
                    if (!bot.cooldowns.a(Items.sv)) {
                        bot.c(EnumHand.b);
                    }
                    //bot.startUsingItem(ProjectileUtil.getWeaponHoldingHand(bot, Items.BOW))
                }
            }
        }

    private void checkAndPerformAttack(EntityLiving target) {
        if (this.ticksUntilNextAttack <= 0) {
            this.resetAttackCooldown(new Random().nextInt(3, 5) * 20);
            this.bot.a(EnumHand.a);
            this.bot.z(target);
            this.attackRadiusSqr = (range * range) * 10f;
        }
    }

    private void resetAttackCooldown(int ticks) {
        this.ticksUntilNextAttack = a(ticks);
    }

    private boolean isTimeToAttack() {return this.ticksUntilNextAttack <= 0;}
}
