package com.magicbanana666.morphingtime.core.network.fabric;

import com.magicbanana666.morphingtime.core.network.ModernNetworking;
import com.magicbanana666.morphingtime.core.network.ModernNetworking.PacketPayload;
import com.magicbanana666.morphingtime.core.network.ModernNetworkingService;
import com.magicbanana666.morphingtime.core.network.fabric.client.ClientModernNetworkingImpl;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.api.EnvType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static com.magicbanana666.morphingtime.core.network.ModernNetworking.getType;

@SuppressWarnings("unused")
public class ModernNetworkingImpl implements ModernNetworkingService {
    // Fabric allows each payload type to be registered only once per direction
    private static final Set<Identifier> CLIENTBOUND_TYPES = new HashSet<>();
    private static final Set<Identifier> SERVERBOUND_TYPES = new HashSet<>();

    private static synchronized void registerClientboundType(Identifier id) {
        if (CLIENTBOUND_TYPES.add(id)) {
            PayloadTypeRegistry.clientboundPlay().register(getType(id), PacketPayload.streamCodec());
        }
    }

    private static synchronized void registerServerboundType(Identifier id) {
        if (SERVERBOUND_TYPES.add(id)) {
            PayloadTypeRegistry.serverboundPlay().register(getType(id), PacketPayload.streamCodec());
        }
    }

    @Override
    public void registerReceiver(ModernNetworking.Side side, Identifier id, ModernNetworking.Receiver receiver) {
        if (side == ModernNetworking.Side.C2S) {
            registerServerboundType(id);
            ServerPlayNetworking.registerGlobalReceiver(getType(id), (payload, context) -> receiver.receive(new ModernNetworking.Context() {
                @Override
                public Player getPlayer() {
                    return context.player();
                }

                @Override
                public ModernNetworking.Env getEnv() {
                    return ModernNetworking.Env.SERVER;
                }

                @Override
                public void queue(Runnable runnable) {
                    context.server().execute(runnable);
                }
            }, payload.nbt()));
        } else if (side == ModernNetworking.Side.S2C) {
            registerClientboundType(id);
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                ClientModernNetworkingImpl.registerReceiver(id, receiver);
            }
        }
    }

    @Override
    public void registerType(Identifier id) {
        // the server has to know the type to send it, the client registers it together with its receiver
        registerClientboundType(id);
    }

    @Contract("_, _ -> new")
    @Override
    @ApiStatus.Internal
    public @NotNull Packet<?> toPacket(ModernNetworking.Side side, CustomPacketPayload payload) {
        if (side == ModernNetworking.Side.C2S) {
            return ClientModernNetworkingImpl.toPacket(payload);
        } else {
            return ServerPlayNetworking.createClientboundPacket(payload);
        }
    }
}
