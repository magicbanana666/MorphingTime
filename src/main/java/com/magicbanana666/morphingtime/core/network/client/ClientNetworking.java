package com.magicbanana666.morphingtime.core.network.client;

import com.magicbanana666.morphingtime.core.client.MorphCoreClient;
import com.magicbanana666.morphingtime.core.network.ModernNetworking;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;


public class ClientNetworking {

    public static void runOrQueue(ModernNetworking.@NotNull Context context, ApplicablePacket packet) {
        if (context.getPlayer() == null) {
            MorphCoreClient.getSyncPacketQueue().add(packet);
        } else {
            context.queue(() -> packet.apply(context.getPlayer()));
        }
    }

    @FunctionalInterface
    public interface ApplicablePacket {
        void apply(Player player);
    }
}
