package com.magicbanana666.morphingtime.core.network.fabric.client;

import com.magicbanana666.morphingtime.core.network.ModernNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import static com.magicbanana666.morphingtime.core.network.ModernNetworking.getType;

/** The client half of the networking, kept apart so the dedicated server never loads the client networking classes. */
@ApiStatus.Internal
public final class ClientModernNetworkingImpl {
    private ClientModernNetworkingImpl() {
    }

    public static void registerReceiver(Identifier id, ModernNetworking.Receiver receiver) {
        ClientPlayNetworking.registerGlobalReceiver(getType(id), (payload, context) -> receiver.receive(new ModernNetworking.Context() {
            @Override
            public Player getPlayer() {
                return context.player();
            }

            @Override
            public ModernNetworking.Env getEnv() {
                return ModernNetworking.Env.CLIENT;
            }

            @Override
            public void queue(Runnable runnable) {
                context.client().execute(runnable);
            }
        }, payload.nbt()));
    }

    public static Packet<?> toPacket(CustomPacketPayload payload) {
        return ClientPlayNetworking.createServerboundPacket(payload);
    }
}
