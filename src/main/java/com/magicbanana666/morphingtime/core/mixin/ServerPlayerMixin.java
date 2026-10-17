package com.magicbanana666.morphingtime.core.mixin;

import com.magicbanana666.morphingtime.core.data.PlayerDataProvider;
import com.magicbanana666.morphingtime.core.data.PlayerDataSynchronizer;
import com.magicbanana666.morphingtime.core.event.common.PlayerEvents;
import com.magicbanana666.morphingtime.core.registration.PlayerDataRegistry;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"DataFlowIssue", "unused", "RedundantCast"})
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void serverTick(CallbackInfo info) {
        PlayerDataSynchronizer.sync((ServerPlayer) (Object) this);
    }

    @Inject(method = "restoreFrom", at = @At("RETURN"))
    private void restoreFrom(ServerPlayer serverPlayer, boolean bl, CallbackInfo ci) {
        PlayerEvents.PLAYER_RESPAWN.invoke().clone(serverPlayer, (ServerPlayer) (Object) this);
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void copyPlayerData(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        PlayerDataProvider oldDataProvider = (PlayerDataProvider) oldPlayer;
        PlayerDataProvider newDataProvider = (PlayerDataProvider) (Object) this;

        for (String key : oldDataProvider.core$keySet()) {
            if (PlayerDataRegistry.isKeyPersistent(key)) {
                // We're now dealing with the actual Java object, so no Class argument is needed here
                newDataProvider.core$writeTag(key, oldDataProvider.core$readTag(key));
            }
        }
    }
}