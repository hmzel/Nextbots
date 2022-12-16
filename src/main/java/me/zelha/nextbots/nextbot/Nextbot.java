package me.zelha.nextbots.nextbot;

import hm.zelha.particlesfx.util.LocationSafe;
import me.zelha.nextbots.Main;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class Nextbot extends EntityZombie {

    private final NextbotDisplay display;
    private final ItemStack kbItem = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.STICK));
    private BukkitTask animator = null;
    int hasntMoved = 0;
    int flyingMenacingly = 0;

    public Nextbot(LocationSafe center, Object obj) {
        super(((CraftWorld) center.getWorld()).getHandle());

        Main.registerBot(this);
        setPosition(center.getX(), center.getY(), center.getZ());

        if (obj instanceof File) {
            display = new NextbotDisplay(center, (File) obj);
        } else {
            display = new NextbotDisplay(center, (String) obj);
        }

        persistent = true;
        canPickUpLoot = false;
        List goalB = (List) getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
        List goalC = (List) getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
        List targetB = (List) getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
        List targetC = (List) getPrivateField("c", PathfinderGoalSelector.class, targetSelector);

        goalB.clear();
        goalC.clear();
        targetB.clear();
        targetC.clear();
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0D, false));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(1, new PathfinderGoalBreakDoor(this));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true, true));
        ((Map) getPrivateField("c", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put("Nextbot", Nextbot.class);
        ((Map) getPrivateField("d", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(Nextbot.class, "Nextbot");
        ((Map) getPrivateField("f", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(Nextbot.class, 54);
        addEffect(new MobEffect(PotionEffectType.INVISIBILITY.getId(), Integer.MAX_VALUE, 1, true, true));
        getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(13131313);
        kbItem.addEnchantment(Enchantment.KNOCKBACK, 127);
        ((CraftWorld) center.getWorld()).getHandle().addEntity(this);

        startAI();
    }

    public void startAI() {
        animator = new BukkitRunnable() {
            @Override
            public void run() {
                Location center = display.getCenter();
                Player nearest = null;
                double dist = Double.MAX_VALUE;

                addEffect(new MobEffect(PotionEffectType.INVISIBILITY.getId(), Integer.MAX_VALUE, 1, true, true));
                center.setX(Nextbot.this.locX);
                center.setY(Nextbot.this.locY + (Nextbot.this.length / 2) + (display.getXRadius() / 2));
                center.setZ(Nextbot.this.locZ);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) continue;

                    double currentDist = center.distanceSquared(player.getLocation());

                    if (dist > currentDist) {
                        nearest = player;
                        dist = currentDist;
                    }
                }

                if (nearest == null) return;

                getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(Math.sqrt(dist));
                setGoalTarget(((CraftPlayer) nearest).getHandle(), EntityTargetEvent.TargetReason.CUSTOM, false);
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    public void despawn() {
        Main.unregisterBot(this);
        display.stop();
        animator.cancel();
        die();
        setHealth(0);

        deathTicks = 100;
    }

    @Override
    public void t_() {
        super.t_();
        super.t_();
        super.t_();
        super.t_();
        super.t_();
        super.t_();
        super.t_();
        super.t_();
        super.t_();
        super.t_();

        if (getGoalTarget() != null && this.locX == this.lastX && this.locZ == this.lastZ) {
            hasntMoved++;
        }

        if (getGoalTarget() != null && hasntMoved >= 300) {
            if (flyingMenacingly >= 80) {
                flyingMenacingly = 0;
                hasntMoved = 0;

                return;
            }

            double motX = getGoalTarget().locX - this.locX;
            double motY = getGoalTarget().locY - this.locY;
            double motZ = getGoalTarget().locZ - this.locZ;

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

            move(motX, motY, motZ);

            flyingMenacingly++;
        }
    }

    @Override
    public boolean V() {
        if (getGoalTarget() == null) return inWater;

        if (getGoalTarget().locY <= this.locY
           && getGoalTarget().locX - 1 < this.locX
           && getGoalTarget().locX + 1 > this.locX
           && getGoalTarget().locZ - 1 < this.locZ
           && getGoalTarget().locZ + 1 > this.locZ) return false;

        return inWater;
    }

    @Override
    public void g(float f, float f1) {
        double d0 = this.locY;
        float f3;
        float f4;

        if (this.bM()) {
            if (this.V() || this.ab()) {
                f3 = 0.8F;
                f4 = 0.02F;
                f3 += (0.54600006F - f3) * 2.5F / 3.0F;
                f4 += (this.bI() - f4) * 2.5F / 3.0F;

                this.a(f, f1, f4);
                this.move(this.motX, this.motY, this.motZ);
                this.motX *= f3;
                this.motY *= 0.800000011920929D;
                this.motZ *= f3;
                this.motY -= 0.02D;

                if (this.positionChanged && this.c(this.motX, this.motY + 0.6000000238418579D - this.locY + d0, this.motZ)) {
                    this.motY = 0.30000001192092896D;
                }
            } else {
                float f5 = 0.91F;

                if (this.onGround) {
                    f5 = this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.getBoundingBox().b) - 1, MathHelper.floor(this.locZ))).getBlock().frictionFactor * 0.91F;
                }

                float f6 = 0.16277136F / (f5 * f5 * f5);

                if (this.onGround) {
                    f3 = this.bI() * f6;
                } else {
                    f3 = this.aM;
                }

                this.a(f, f1, f3);

                f5 = 0.91F;

                if (this.onGround) {
                    f5 = this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.getBoundingBox().b) - 1, MathHelper.floor(this.locZ))).getBlock().frictionFactor * 0.91F;
                }

                if (this.k_()) {
                    f4 = 0.15F;
                    this.motX = MathHelper.a(this.motX, -f4, f4);
                    this.motZ = MathHelper.a(this.motZ, -f4, f4);
                    this.fallDistance = 0.0F;

                    if (this.motY < -0.015D) {
                        this.motY = -0.015D;
                    }

                    boolean flag = this.isSneaking();

                    if (flag && this.motY < 0.0D) {
                        this.motY = 0.0D;
                    }
                }

                this.move(this.motX, this.motY, this.motZ);

                if (this.positionChanged && this.k_()) {
                    this.motY = 0.2D;
                }

                if (!this.world.isClientSide || this.world.isLoaded(new BlockPosition((int) this.locX, 0, (int) this.locZ)) && this.world.getChunkAtWorldCoords(new BlockPosition((int) this.locX, 0, (int) this.locZ)).o()) {
                    this.motY -= 0.008D;
                } else if (this.locY > 0.0D) {
                    this.motY = -0.01D;
                } else {
                    this.motY = 0.0D;
                }

                this.motY *= 0.9200000190734863D;
                this.motX *= f5;
                this.motZ *= f5;
            }
        }

        this.aA = this.aB;
        d0 = this.locX - this.lastX;
        double d1 = this.locZ - this.lastZ;
        float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        this.aB += (f2 - this.aB) * 0.4F;
        this.aC += this.aB;
    }

    @Override
    public boolean r(Entity entity) {
        flyingMenacingly = 0;
        hasntMoved = 0;

        return super.r(entity);
    }

    @Override
    public ItemStack bA() {
        return kbItem;
    }

    @Override
    public void a(boolean flag) {
    }

    @Override
    public void setOnFire(int i) {
    }

    @Override
    public void makeSound(String s, float f, float f1) {
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return false;
    }

    private Object getPrivateField(String fieldName, Class clazz, Object object) {
        try {
            Field field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);

            return field.get(object);
        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
