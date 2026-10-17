package com.magicbanana666.morphingtime.skin.mixin.client;

import com.magicbanana666.morphingtime.skin.data.SkinPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {
    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    public void setToNewSkin(@NotNull CallbackInfoReturnable<PlayerSkin> cir) {
        @NotNull CompletableFuture<Optional<PlayerSkin>> skinFuture = SkinPlayerData.getPlayerSkin(Minecraft.getInstance().services().profileResolver(), (Player) (Object) this);
        Optional<PlayerSkin> playerSkin = skinFuture.getNow(Optional.empty());
        playerSkin.ifPresent(cir::setReturnValue);
    }
}
