package com.magicbanana666.morphingtime;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;

/** Server -> client: the player just unlocked the shape of this (dying) entity, show the unlock animation. */
public record UnlockEffectPayload(int entityId) implements CustomPacketPayload {

    public static final Type<UnlockEffectPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MorphingTime.MODID, "unlock_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UnlockEffectPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, UnlockEffectPayload::entityId,
            UnlockEffectPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void send(ServerPlayer player, Entity unlocked) {
        PacketDistributor.sendToPlayer(player, new UnlockEffectPayload(unlocked.getId()));
    }
}
