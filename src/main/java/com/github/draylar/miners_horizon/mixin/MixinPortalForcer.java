package com.github.draylar.miners_horizon.mixin;

import com.github.draylar.miners_horizon.MinersHorizon;
import com.github.draylar.miners_horizon.util.TeleportPlacementHandler;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PortalForcer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PortalForcer.class)
public class MixinPortalForcer
{
    @Shadow
    @Final
    private ServerWorld world;

    @Inject(method = "usePortal", at = @At("HEAD"), cancellable = true)
    private void usePortal(Entity entity, float float_1, CallbackInfoReturnable<Boolean> infoReturnable)
    {
        // going to the void world
        if (world.getDimension().getType() == MinersHorizon.FABRIC_WORLD)
        {
            TeleportPlacementHandler.enterDimension(entity, (ServerWorld) entity.getEntityWorld(), world);
            infoReturnable.setReturnValue(true);
            infoReturnable.cancel();
        }

        // coming from the void world
        if (entity.getEntityWorld().getDimension().getType() == MinersHorizon.FABRIC_WORLD)
        {
            TeleportPlacementHandler.leaveDimension(entity, (ServerWorld) entity.getEntityWorld(), world);
            infoReturnable.setReturnValue(true);
            infoReturnable.cancel();
        }
    }
}
