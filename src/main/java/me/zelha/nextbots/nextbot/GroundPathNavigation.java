package me.zelha.nextbots.nextbot;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;

public class GroundPathNavigation extends PathNavigation {
    public GroundPathNavigation(Mob var0, Level var1) {
        super(var0, var1);
    }

    @Override
    public void recomputePath() {
        BlockPos targetPos;
        int reachRange;

        try {
            Field f = PathNavigation.class.getDeclaredField("targetPos");

            f.setAccessible(true);

            targetPos = (BlockPos) f.get(this);
            f = PathNavigation.class.getDeclaredField("reachRange");

            f.setAccessible(true);

            reachRange = f.getInt(this);
        } catch (Throwable e) {
            super.recomputePath();

            return;
        }

        if (this.level.getGameTime() - this.timeLastRecompute != 0) {
            if (targetPos != null) {
                this.path = null;
                this.path = this.createPath(targetPos, reachRange);
                this.timeLastRecompute = this.level.getGameTime();
                this.hasDelayedRecomputation = false;
            }
        } else {
            this.hasDelayedRecomputation = true;
        }
    }

    protected PathFinder createPathFinder(int var0) {
        this.nodeEvaluator = new WalkNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);

        return new PathFinder(this.nodeEvaluator, var0);
    }

    protected boolean canUpdatePath() {
        return this.mob.isOnGround() || this.isInLiquid() || this.mob.isPassenger();
    }

    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.getX(), this.getSurfaceY(), this.mob.getZ());
    }

    public Path createPath(BlockPos var0, int var1) {
        BlockPos var2;

        if (this.level.getBlockState(var0).isAir()) {
            for (var2 = var0.below(); var2.getY() > this.level.getMinBuildHeight() && this.level.getBlockState(var2).isAir(); var2 = var2.below()) {
            }

            if (var2.getY() > this.level.getMinBuildHeight()) {
                return super.createPath(var2.above(), var1);
            }

            while(var2.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState(var2).isAir()) {
                var2 = var2.above();
            }

            var0 = var2;
        }

        if (!this.level.getBlockState(var0).getMaterial().isSolid()) {
            return super.createPath(var0, var1);
        } else {
            for(var2 = var0.above(); var2.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState(var2).getMaterial().isSolid(); var2 = var2.above()) {
            }

            return super.createPath(var2, var1);
        }
    }

    public Path createPath(Entity var0, int var1) {
        return this.createPath(var0.blockPosition(), var1);
    }

    private int getSurfaceY() {
        if (this.mob.isInWater() && this.canFloat()) {
            int var0 = this.mob.getBlockY();
            BlockState var1 = this.level.getBlockState(new BlockPos(this.mob.getX(), var0, this.mob.getZ()));
            int var2 = 0;

            do {
                if (!var1.is(Blocks.WATER)) {
                    return var0;
                }

                var0++;
                var1 = this.level.getBlockState(new BlockPos(this.mob.getX(), (double)var0, this.mob.getZ()));
                var2++;
            } while (var2 <= 16);

            return this.mob.getBlockY();
        } else {
            return Mth.floor(this.mob.getY() + 0.5D);
        }
    }
}
