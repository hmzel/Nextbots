package me.zelha.nextbots.nextbot;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.DoorInteractGoal;
import net.minecraft.world.level.block.Block;
import org.bukkit.craftbukkit.v1_19_R2.event.CraftEventFactory;

public class BreakDoorGoal extends DoorInteractGoal {
    protected int breakTime;
    protected int lastBreakProgress;
    protected int doorBreakTime;

    public BreakDoorGoal(Mob entityinsentient) {
        super(entityinsentient);

        this.lastBreakProgress = -1;
        this.doorBreakTime = -1;
    }

    public BreakDoorGoal(Mob entityinsentient, int i) {
        this(entityinsentient);

        this.doorBreakTime = i;
    }

    protected int getDoorBreakTime() {
        return Math.max(240, this.doorBreakTime);
    }

    public boolean canUse() {
        return super.canUse() && !this.isOpen();
    }

    public void start() {
        super.start();

        this.breakTime = 0;
    }

    public boolean canContinueToUse() {
        return this.breakTime <= this.getDoorBreakTime() && !this.isOpen() && this.doorPos.closerToCenterThan(this.mob.position(), 2.0D);
    }

    public void stop() {
        super.stop();
        this.mob.level.destroyBlockProgress(this.mob.getId(), this.doorPos, -1);
    }

    public void tick() {
        super.tick();

        if (this.mob.getRandom().nextInt(20) == 0) {
            this.mob.level.levelEvent(1019, this.doorPos, 0);

            if (!this.mob.swinging) {
                this.mob.swing(this.mob.getUsedItemHand());
            }
        }

        this.breakTime++;
        int i = (int)((float)this.breakTime / (float)this.getDoorBreakTime() * 10.0F);

        if (i != this.lastBreakProgress) {
            this.mob.level.destroyBlockProgress(this.mob.getId(), this.doorPos, i);

            this.lastBreakProgress = i;
        }

        if (this.breakTime == this.getDoorBreakTime()) {
            if (CraftEventFactory.callEntityBreakDoorEvent(this.mob, this.doorPos).isCancelled()) {
                this.start();

                return;
            }

            this.mob.level.removeBlock(this.doorPos, false);
            this.mob.level.levelEvent(1021, this.doorPos, 0);
            this.mob.level.levelEvent(2001, this.doorPos, Block.getId(this.mob.level.getBlockState(this.doorPos)));
        }
    }
}
