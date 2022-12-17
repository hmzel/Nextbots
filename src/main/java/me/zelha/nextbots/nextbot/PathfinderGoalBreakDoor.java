package me.zelha.nextbots.nextbot;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;

public class PathfinderGoalBreakDoor extends PathfinderGoalDoorInteract {
    private int g;
    private int h = -1;

    public PathfinderGoalBreakDoor(EntityInsentient entityinsentient) {
        super(entityinsentient);
    }

    public boolean a() {
        if (!super.a()) {
            return false;
        } else {
            return !BlockDoor.f(this.a.world, this.b);
        }
    }

    public void c() {
        super.c();

        this.g = 0;
    }

    public boolean b() {
        return this.g <= 240 && !BlockDoor.f(this.a.world, this.b) && this.a.b(this.b) < 4.0D;
    }

    public void d() {
        super.d();
        this.a.world.c(this.a.getId(), this.b, -1);
    }

    public void e() {
        super.e();

        if (this.a.bc().nextInt(20) == 0) {
            this.a.world.triggerEffect(1010, this.b, 0);
        }

        this.g++;
        int i = (int)((float)this.g / 240.0F * 10.0F);

        if (i != this.h) {
            this.a.world.c(this.a.getId(), this.b, i);
            this.h = i;
        }

        if (this.g == 240) {
            if (CraftEventFactory.callEntityBreakDoorEvent(this.a, this.b.getX(), this.b.getY(), this.b.getZ()).isCancelled()) {
                this.c();
                return;
            }

            this.a.world.setAir(this.b);
            this.a.world.triggerEffect(1012, this.b, 0);
            this.a.world.triggerEffect(2001, this.b, Block.getId(this.c));
        }
    }
}
