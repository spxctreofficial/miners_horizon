package com.github.draylar.miners_horizon.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * I made a goof up somehow. It seems like when players used 1.3.1 or 1.4.0 of Miner's Horizon, their NBT dimension was saved as null.
 * When the game loads, they would crash here. This serves as a temporary fix and will be removed in a month or two.
 */
@Mixin(Entity.class)
public abstract class NullPlayerWorldFix
{
    @Shadow public DimensionType dimension;

    @Shadow public World world;

    @Shadow public abstract void setPosition(double double_1, double double_2, double double_3);

    @Inject(method = "fromTag", at = @At("RETURN"))
    private void setDimensionIfNull(final CompoundTag compoundTag, final CallbackInfo info)
    {
        if (compoundTag.containsKey("Dimension"))
        {
            DimensionType type = DimensionType.byRawId(compoundTag.getInt("Dimension"));

            if(type == null)
            {
                dimension = DimensionType.OVERWORLD;
                setPosition(world.getSpawnPos().getX(), world.getSpawnPos().getY(), world.getSpawnPos().getZ());
            }
        }
    }
}
