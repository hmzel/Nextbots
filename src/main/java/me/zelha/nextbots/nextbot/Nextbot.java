package me.zelha.nextbots.nextbot;

import hm.zelha.particlesfx.util.LocationSafe;
import me.zelha.nextbots.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class Nextbot extends Drowned {

    private final ThreadLocalRandom rng = ThreadLocalRandom.current();
    private final NextbotDisplay display;
    private final Location lHelper;
    private BukkitTask animator = null;
    int hasntMoved = 0;
    int flyingMenacingly = 0;
    int angry = 0;
    int calm = 0;

    public Nextbot(LocationSafe center, Object obj, String name) {
        super(EntityType.DROWNED, ((CraftWorld) center.getWorld()).getHandle());

        Main.registerBot(this);
        setPos(center.getX(), center.getY(), center.getZ());

        if (obj instanceof File) {
            display = new NextbotDisplay(center, (File) obj);
        } else {
            display = new NextbotDisplay(center, (String) obj);
        }

        lHelper = display.getCenter().clone();
        bukkitPickUpLoot = false;

        goalSelector.getAvailableGoals().clear();
        targetSelector.getAvailableGoals().clear();
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        goalSelector.addGoal(1, new BreakDoorGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, net.minecraft.world.entity.player.Player.class, true, true));
        groundNavigation.setAvoidSun(false);
        groundNavigation.setCanFloat(true);
        waterNavigation.setCanFloat(true);
        setCustomName(CraftChatMessage.fromStringOrNull(name));
        setPersistenceRequired(true);
        setCanPickUpLoot(false);
        setSilent(true);
        addEffect(new MobEffectInstance(MobEffect.byId(PotionEffectType.INVISIBILITY.getId()), Integer.MAX_VALUE, 1, true, true));
        craftAttributes.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(13131313);
        craftAttributes.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(13131313);
        ((CraftWorld) center.getWorld()).getHandle().getMinecraftWorld().addFreshEntity(this);

        start();
    }

    public void start() {
        animator = new BukkitRunnable() {
            @Override
            public void run() {
                Location center = display.getCenter();
                Player nearest = null;
                double dist = Double.MAX_VALUE;

                addEffect(new MobEffectInstance(MobEffect.byId(PotionEffectType.INVISIBILITY.getId()), Integer.MAX_VALUE, 1, true, true));
                center.setX(Nextbot.this.getX());
                center.setY(Nextbot.this.getY() + (Nextbot.this.getBbHeight() / 2) + (display.getZRadius() / 2));
                center.setZ(Nextbot.this.getZ());

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) continue;

                    double currentDist = center.distanceSquared(player.getLocation());

                    if (dist > currentDist) {
                        nearest = player;
                        dist = currentDist;
                    }
                }

                if (nearest == null) return;

                craftAttributes.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(Math.sqrt(dist));
                setTarget(((CraftPlayer) nearest).getHandle(), EntityTargetEvent.TargetReason.CUSTOM, false);
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    public void despawn() {
        Main.unregisterBot(this);
        display.stop();
        animator.cancel();
        die(DamageSource.OUT_OF_WORLD);
        setHealth(0);

        dead = true;
    }

    @Override
    public void tick() {
        super.tick();
        super.tick();
        super.tick();
        super.tick();
        super.tick();
        super.tick();
        super.tick();
        super.tick();
        super.tick();
        super.tick();

        if (getTarget() != null && this.getX() == this.xOld && this.getZ() == this.zOld) {
            hasntMoved++;
        } else if (flyingMenacingly == 0) {
            hasntMoved = 0;
            calm++;
        }

        if (calm >= 30) {
            angry = 0;
        }

        if (getTarget() != null && hasntMoved >= 300) {
            if (flyingMenacingly >= 100 && angry < 3) {
                flyingMenacingly = 0;
                hasntMoved = 0;
                calm = 0;
                angry++;

                return;
            }

            double motX = getTarget().getX() - this.getX();
            double motY = getTarget().getY() - this.getY();
            double motZ = getTarget().getZ() - this.getZ();

            if (motX > 0) {
                motX = Math.min(motX, 1);
            } else {
                motX = Math.max(motX, -1);
            }

            if (motY > 0) {
                motY = Math.min(motY, 2);
            } else {
                motY = Math.max(motY, -2);
            }

            if (motZ > 0) {
                motZ = Math.min(motZ, 1);
            } else {
                motZ = Math.max(motZ, -1);
            }

            if (rng.nextInt(5) == 0) {
                motX = rng.nextInt(2) - 1;
                motZ = rng.nextInt(2) - 1;
            }

            move(MoverType.SELF, new Vec3(motX, motY, motZ));

            for (org.bukkit.entity.Entity entity : getLevel().getWorld().getNearbyEntities(lHelper.zero().add(getX(), getY(), getZ()), getBbWidth(), getBbHeight(), getBbWidth())) {
                if (!(entity instanceof Player)) continue;

                if (((CraftPlayer) entity).getHandle().hurt(DamageSource.mobAttack(this), 13131313)) {
                    flyingMenacingly = 0;
                    hasntMoved = 0;
                    calm = 0;
                    angry = 0;

                    return;
                }
            }

            flyingMenacingly++;
        }
    }

    @Override
    public void move(MoverType moverType, Vec3 vec3d) {
        if (angry >= 3 && flyingMenacingly > 0) {
            for (int x = (int) display.getXRadius(); x >= (int) -display.getXRadius(); x--) {
                for (int y = (int) display.getZRadius(); y >= (int) -display.getZRadius(); y--) {
                    for (int z = (int) display.getXRadius(); z >= (int) -display.getXRadius(); z--) {
                        Block block = lHelper.zero().add(display.getCenter()).add(x, y, z).getBlock();

                        if (block.getType() == Material.AIR) continue;

                        SoundType breakSound = level.getBlockState(new BlockPos(lHelper.getBlockX(), lHelper.getBlockY(), lHelper.getBlockZ())).getSoundType();

                        block.breakNaturally();
                        level.playLocalSound(lHelper.getX(), lHelper.getY(), lHelper.getZ(), breakSound.breakSound, SoundSource.BLOCKS, 1, 1, true);
                    }
                }
            }
        }

        super.move(moverType, vec3d);
    }

    @Override
    public boolean isInWater() {
        if (getTarget() == null) return super.isInWater();
        if (getTarget().getY() <= this.getY()) return false;

        return super.isInWater();
    }

    @Override
    public void travel(Vec3 vec3d) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            double posY = this.getY();
            Vec3 vec3d1;

            if (this.isInWater() || this.isInLava()) {
                float f = 0.8f + ((0.54600006F - 0.8f) * 5 / 3.0F);

                this.moveRelative((0.02f + ((this.getSpeed() - 0.02f) * 5 / 3.0F)), vec3d);
                this.move(MoverType.SELF, this.getDeltaMovement());

                vec3d1 = this.getDeltaMovement();

                if (this.horizontalCollision && this.onClimbable()) {
                    vec3d1 = new Vec3(vec3d1.x, 0.2D, vec3d1.z);
                }

                this.setDeltaMovement(vec3d1.multiply(f, 0.800000011920929D, f));

                Vec3 vec3d2 = this.getFluidFallingAdjustedMovement(0.08D, this.getDeltaMovement().y <= 0.0D, this.getDeltaMovement());

                this.setDeltaMovement(vec3d2);

                if (this.horizontalCollision && this.isFree(vec3d2.x, vec3d2.y + 0.6000000238418579D - this.getY() + posY, vec3d2.z)) {
                    this.setDeltaMovement(vec3d2.x, 0.30000001192092896D, vec3d2.z);
                }
            } else {
                float blockFriction = this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction();
                float friction = this.onGround ? blockFriction * 0.91F : 0.91F;
                vec3d1 = this.handleRelativeFrictionAndCalculateMovement(vec3d, blockFriction);
                double vecY = vec3d1.y;

                if (this.shouldDiscardFriction()) {
                    this.setDeltaMovement(vec3d1.x, vecY, vec3d1.z);
                } else {
                    this.setDeltaMovement(vec3d1.x * (double) friction, vecY * 0.9200000190734863D, vec3d1.z * (double) friction);
                }
            }
        }

        this.calculateEntityAnimation(this, false);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        flyingMenacingly = 0;
        hasntMoved = 0;
        calm = 0;
        angry = 0;

        return super.doHurtTarget(entity);
    }

    @Override
    public boolean okTarget(@Nullable LivingEntity entityliving) {
        return entityliving != null;
    }

    @Override
    public void setSecondsOnFire(int i, boolean callEvent) {
    }

    @Override
    public void setRemainingFireTicks(int i) {
    }

    @Override
    public void setCanBreakDoors(boolean flag) {
        super.setCanBreakDoors(true);
    }

    @Override
    protected boolean supportsBreakDoorGoal() {
        return true;
    }

    @Override
    protected boolean damageEntity0(DamageSource damagesource, float f) {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    public NextbotDisplay getDisplay() {
        return display;
    }
}
