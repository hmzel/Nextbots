package me.zelha.nextbots.nextbot;

import hm.zelha.particlesfx.util.LocationSafe;
import me.zelha.nextbots.Main;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class Nextbot extends EntityZombie {

    private final NextbotDisplay display;
    private BukkitTask animator = null;

    public Nextbot(LocationSafe center, Object obj) {
        super(((CraftWorld) center.getWorld()).getHandle());

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
        display.stop();
        animator.cancel();
        die();
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
