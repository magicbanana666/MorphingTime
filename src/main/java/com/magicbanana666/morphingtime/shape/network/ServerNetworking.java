package com.magicbanana666.morphingtime.shape.network;

import com.magicbanana666.morphingtime.core.event.common.PlayerEvents;
import com.magicbanana666.morphingtime.core.network.ModernNetworking;
import com.magicbanana666.morphingtime.shape.api.PlayerAbilities;
import com.magicbanana666.morphingtime.shape.network.impl.SwapPackets;
import com.magicbanana666.morphingtime.shape.network.impl.SwapVariantPackets;
import com.magicbanana666.morphingtime.shape.network.impl.SyncApiLevelPackets;
import com.magicbanana666.morphingtime.shape.network.impl.UnlockPackets;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ServerNetworking implements NetworkHandler {

    public static void initialize() {
        SwapPackets.registerWalkersRequestPacketHandler();
        UnlockPackets.registerShapeUnlockRequestPacketHandler();
        SwapVariantPackets.registerSwapVariantPacketHandler();
        registerUseAbilityPacketHandler();

        ModernNetworking.registerType(SHAPE_SYNC);
        ModernNetworking.registerType(ABILITY_SYNC);
        ModernNetworking.registerType(UNLOCK_SYNC);
        ModernNetworking.registerType(SYNC_API_LEVEL);

        // sync API level and second shape
        PlayerEvents.PLAYER_JOIN.register(player -> {
            SyncApiLevelPackets.sendSyncPacket(player);
            UnlockPackets.sendSyncPacket(player);
        });
    }

    @SuppressWarnings("ConstantConditions")
    private static void registerUseAbilityPacketHandler() {
        ModernNetworking.registerReceiver(ModernNetworking.Side.C2S, USE_ABILITY, (context, packet) -> {
            Player player = context.getPlayer();

            ((net.minecraft.server.level.ServerLevel) context.getPlayer().level()).getServer().execute(() -> PlayerAbilities.useAbility((ServerPlayer) player));
        });
    }
}
