package com.magicbanana666.morphingtime.collection.mixin.client.accessor;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(AbstractClientPlayer.class)
public interface ClientPlayerAccessor {
    @Invoker
    PlayerInfo callGetPlayerInfo();
}
