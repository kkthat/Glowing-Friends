package com.glowingfriends.mixin;

import com.glowingfriends.util.GlowingManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void onHasOutline(CallbackInfoReturnable<Boolean> info) {
        if ((Object) this instanceof PlayerEntity) {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity clientPlayer = client.player;

            if (clientPlayer != null && (Entity) (Object) this != clientPlayer) {
                PlayerEntity player = (PlayerEntity) (Object) this;
                UUID playerUUID = player.getUuid();
                if (GlowingManager.isPlayerGlowing(playerUUID)) {
                    info.setReturnValue(true);
                }
            }
        }
    }
}