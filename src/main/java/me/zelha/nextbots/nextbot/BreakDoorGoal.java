package me.zelha.nextbots.nextbot;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.DoorInteractGoal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
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

    protected int getDoorBreakTime() {
        return Math.max(240, this.doorBreakTime);
    }

    public boolean canUse() {
        if (!this.mob.horizontalCollision) return false;

        Path var1 = this.mob.getNavigation().getPath();

        if (var1 != null && !var1.isDone()) {
            for (int var2 = 0; var2 < Math.min(var1.getNextNodeIndex() + 2, var1.getNodeCount()); ++var2) {
                Node var3 = var1.getNode(var2);
                this.doorPos = new BlockPos(var3.x, var3.y + 1, var3.z);

                if (!(this.mob.distanceToSqr(this.doorPos.getX(), this.mob.getY(), this.doorPos.getZ()) > 2.25D)) {
                    this.hasDoor = DoorBlock.isWoodenDoor(this.mob.level, this.doorPos);

                    if (this.hasDoor) {
                        return true;
                    }
                }
            }

            this.doorPos = this.mob.blockPosition().above();
            this.hasDoor = DoorBlock.isWoodenDoor(this.mob.level, this.doorPos);

            return this.hasDoor;
        } else {
            return false;
        }
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
