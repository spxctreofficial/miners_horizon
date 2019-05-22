package com.github.draylar.miners_horizon.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * I made a goof up somehow. It seems like when players used 1.3.1 or 1.4.0 of Miner's Horizon, their NBT dimension was saved as null.
 * When the game loads, they would crash here. This serves as a temporary fix and will be removed in a month or two.
 */
@Mixin(MinecraftServer.class)
public class NullDimensionFix
{
    @Shadow @Final private Map<DimensionType, ServerWorld> worlds;

    @Inject(at = @At("HEAD"), method = "getWorld")
    private void onPlayerConnect(DimensionType dimensionType_1, CallbackInfoReturnable<ServerWorld> cir)
    {
        if(dimensionType_1 == null)
        {
            cir.setReturnValue(this.worlds.get(DimensionType.OVERWORLD));
        }
    }
}
