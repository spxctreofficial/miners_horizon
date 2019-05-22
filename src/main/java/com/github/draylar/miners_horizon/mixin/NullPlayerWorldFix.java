package com.github.draylar.miners_horizon.mixin;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * I made a goof up somehow. It seems like when players used 1.3.1 or 1.4.0 of Miner's Horizon, their NBT dimension was saved as null.
 * When the game loads, they would crash here. This serves as a temporary fix and will be removed in a month or two.
 */
@Mixin(PlayerManager.class)
public abstract class NullPlayerWorldFix
{
    @Shadow @Final private MinecraftServer server;

    @ModifyVariable(method = "onPlayerConnect", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/PlayerManager;loadPlayerData(Lnet/minecraft/server/network/ServerPlayerEntity;)Lnet/minecraft/nbt/CompoundTag;"))
    private ServerPlayerEntity changeWorld(ServerPlayerEntity serverPlayerEntity_1)
    {
        if(serverPlayerEntity_1.dimension == null)
            serverPlayerEntity_1.dimension = DimensionType.OVERWORLD;

        // give player fall resistance for 10 seconds so they don't die
        serverPlayerEntity_1.addPotionEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 10, 4));

        return serverPlayerEntity_1;
    }
}
