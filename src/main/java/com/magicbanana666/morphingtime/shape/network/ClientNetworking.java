package com.magicbanana666.morphingtime.shape.network;

import com.magicbanana666.morphingtime.core.client.MorphCoreClient;
import com.magicbanana666.morphingtime.core.network.ModernNetworking;
import com.magicbanana666.morphingtime.core.network.client.ClientNetworking.ApplicablePacket;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.impl.PlayerDataProvider;
import com.magicbanana666.morphingtime.shape.network.impl.SyncApiLevelPackets;
import com.magicbanana666.morphingtime.shape.network.impl.UnlockPackets;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class ClientNetworking implements NetworkHandler {

    public static void registerPacketHandlers() {
        ModernNetworking.registerReceiver(ModernNetworking.Side.S2C, NetworkHandler.SHAPE_SYNC,
                ClientNetworking::handleWalkersSyncPacket);
        ModernNetworking.registerReceiver(ModernNetworking.Side.S2C, NetworkHandler.ABILITY_SYNC,
                ClientNetworking::handleAbilitySyncPacket);
        ModernNetworking.registerReceiver(ModernNetworking.Side.S2C, NetworkHandler.UNLOCK_SYNC,
                UnlockPackets::handleUnlockSyncPacket);
        ModernNetworking.registerReceiver(ModernNetworking.Side.S2C, NetworkHandler.SYNC_API_LEVEL,
                (context, nbt) -> SyncApiLevelPackets.handleSyncPacket(nbt));
    }

    public static void runOrQueue(ModernNetworking.Context context, ApplicablePacket packet) {
        if (context.getPlayer() == null) {
            MorphCoreClient.getSyncPacketQueue().add(packet);
        } else {
            context.queue(() -> packet.apply(context.getPlayer()));
        }
    }

    public static void sendAbilityRequest() {
        ModernNetworking.sendToServer(USE_ABILITY, new CompoundTag());
    }

    public static void handleWalkersSyncPacket(ModernNetworking.Context context, CompoundTag packetData) {
        final UUID uuid = UUIDUtil.uuidFromIntArray(packetData.getIntArray("uuid").orElseThrow());
        final String id = packetData.getString("type").orElseThrow();
        final CompoundTag entityNbt = packetData.getCompound("entity_tag").orElseThrow();
        final boolean special_anim = packetData.getBoolean("special_anim").orElse(false);

        runOrQueue(context, player -> {
            @Nullable
            Player syncTarget = player.level().getPlayerByUUID(uuid);

            if (syncTarget != null) {
                PlayerDataProvider data = (PlayerDataProvider) syncTarget;
                data.shape$setIsSpecialAnim(special_anim);

                // set shape to null (no shape) if the entity id is "minecraft:empty"
                if (id.equals("minecraft:empty")) {
                    data.shape$setCurrentShape(null);
                    syncTarget.refreshDimensions();
                    return;
                }

                // If entity type was valid, deserialize entity data from tag
                entityNbt.putString("id", id);
                ValueInput in = TagValueInput.create(Shapes.PROBLEM_REPORTER, player.registryAccess(), entityNbt);
                Optional<EntityType<?>> type = EntityType.by(in);
                if (type.isPresent()) {
                    LivingEntity shape = data.shape$getCurrentShape();

                    // ensure entity data exists
                    if (shape == null || !type.get().equals(shape.getType())) {
                        shape = (LivingEntity) type.get().create(syncTarget.level(), EntitySpawnReason.LOAD);
                        data.shape$setCurrentShape(shape);

                        // refresh player dimensions/hitbox on client
                        syncTarget.refreshDimensions();
                    }

                    if (shape != null) {
                        shape.load(in);
                    }
                }
            }
        });
    }

    public static void handleAbilitySyncPacket(ModernNetworking.Context context, CompoundTag packet) {
        int cooldown = packet.getInt("cooldown").orElseThrow();
        runOrQueue(context, player -> ((PlayerDataProvider) player).shape$setAbilityCooldown(cooldown));
    }
}
