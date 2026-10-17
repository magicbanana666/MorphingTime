package com.magicbanana666.morphingtime.core.client;

import com.magicbanana666.morphingtime.core.MorphCore;
import com.magicbanana666.morphingtime.core.config.ConfigLoader;
import com.magicbanana666.morphingtime.core.data.PlayerDataSynchronizer;
import com.magicbanana666.morphingtime.core.event.client.ClientPlayerEvents;
import com.magicbanana666.morphingtime.core.event.client.RenderEvents;
import com.magicbanana666.morphingtime.core.network.ModernNetworking;
import com.magicbanana666.morphingtime.core.network.client.ClientNetworking.ApplicablePacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;

import java.util.HashSet;
import java.util.Set;


public class MorphCoreClient {
    private static final Set<ApplicablePacket> SYNC_PACKET_QUEUE = new HashSet<>();

    public void initialize() {
        ConfigLoader.registerConfigSyncHandler();
        PlayerDataSynchronizer.registerPacketHandler();

        ClientPlayerEvents.CLIENT_PLAYER_JOIN.register(player -> {
            for (ApplicablePacket packet : getSyncPacketQueue()) {
                packet.apply(player);
            }

            getSyncPacketQueue().clear();
        });

        // prevent full air bar when swimming
        RenderEvents.RENDER_BREATH.register((graphics, player) -> {
            if (player != null && player.getAirSupply() == player.getMaxAirSupply() && player.isEyeInFluid(FluidTags.WATER)) {
                return InteractionResult.FAIL;
            } else {
                return InteractionResult.PASS;
            }
        });


        ModernNetworking.registerReceiver(ModernNetworking.Side.S2C, MorphCore.CLEAR_CACHE_PACKET, (context, data) -> MorphCore.clearCache());
    }

    public static Set<ApplicablePacket> getSyncPacketQueue() {
        return SYNC_PACKET_QUEUE;
    }
}
