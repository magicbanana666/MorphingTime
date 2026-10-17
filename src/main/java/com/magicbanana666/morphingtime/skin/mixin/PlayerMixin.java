package com.magicbanana666.morphingtime.skin.mixin;

import com.mojang.authlib.GameProfile;
import com.magicbanana666.morphingtime.skin.PlayerSkins;
import com.magicbanana666.morphingtime.skin.data.SkinPlayerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow
    protected abstract MutableComponent decorateDisplayNameComponent(MutableComponent nameComponent);

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void onGetName(CallbackInfoReturnable<Component> cir) {
        if (PlayerSkins.CONFIG.changeName) {
            Optional<GameProfile> profile = SkinPlayerData.getSkinProfile((Player) (Object) this);
            profile.ifPresent(p -> cir.setReturnValue(decorateDisplayNameComponent(Component.literal((p.name())))));
        }
    }
}
