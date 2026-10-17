package com.magicbanana666.morphingtime.shape.network.impl;

import com.magicbanana666.morphingtime.core.network.ModernNetworking;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.api.PlayerShapeChanger;
import com.magicbanana666.morphingtime.shape.api.blacklist.EntityBlacklist;
import com.magicbanana666.morphingtime.shape.api.platform.ApiLevel;
import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import com.magicbanana666.morphingtime.shape.impl.PlayerDataProvider;
import com.magicbanana666.morphingtime.shape.network.ClientNetworking;
import com.magicbanana666.morphingtime.shape.network.NetworkHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnlockPackets {

    private static final String UNLOCK_KEY = "UnlockedShape";
    // Extra reach for the unlock key check, the mob may have moved a bit while the packet was on its way
    private static final double TARGET_RANGE_MARGIN = 2.0;
    private static boolean handlingUnlockKey = false;

    public static void handleUnlockSyncPacket(ModernNetworking.Context context, CompoundTag nbt) {
        if (nbt != null && ApiLevel.getCurrentLevel().canUnlock) {
            CompoundTag idTag = nbt.getCompound(UNLOCK_KEY).orElse(new CompoundTag());

            ClientNetworking.runOrQueue(context, player -> {
                if (!idTag.isEmpty()) {
                    ((PlayerDataProvider) player).shape$set2ndShape(ShapeType.from(idTag));
                }
            });
        }
    }

    /**
     * Server handles a request to change the second shape
     */
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static void registerShapeUnlockRequestPacketHandler() {
        ModernNetworking.registerReceiver(ModernNetworking.Side.C2S, NetworkHandler.UNLOCK_REQUEST, (context, nbt) -> {
            // check if player is blacklisted
            if (Shapes.isPlayerBlacklisted(context.getPlayer().getUUID()) && Shapes.CONFIG.blacklistPreventsUnlocking) {
                return;
            }

            if (!ApiLevel.getCurrentLevel().canUnlock) {
                return;
            }

            boolean validType = nbt.getBoolean("valid_type").orElse(false);
            if (validType) {
                Identifier typeId = Identifier.tryParse(nbt.getString("type_id").orElse(""));
                EntityType<?> entityType = typeId == null ? null : BuiltInRegistries.ENTITY_TYPE.get(typeId).map(Holder::value).orElse(null);
                if (entityType == null) {
                    return;
                }

                int variant = nbt.getInt("variant").orElse(-1);

                ((net.minecraft.server.level.ServerLevel) context.getPlayer().level()).getServer().execute(() -> {
                    ServerPlayer player = (ServerPlayer) context.getPlayer();
                    // The player has to be in reach of a real mob of this type, use its shape
                    @Nullable
                    ShapeType<? extends LivingEntity> type = findTarget(player, entityType, variant);
                    if (type != null && !EntityBlacklist.isBlacklisted(type.getEntityType()) && (Shapes.CONFIG.unlockOverridesCurrentShape || ((PlayerDataProvider) player).shape$get2ndShape() == null)) {
                        // set second shape
                        boolean result;
                        handlingUnlockKey = true;
                        try {
                            result = PlayerShapeChanger.change2ndShape(player, type);
                        } finally {
                            handlingUnlockKey = false;
                        }
                        // update Player
                        if (result) {
                            sendSyncPacket(player);
                            PlayerShape.updateShapes(player, type.create(player.level(), player));
                        }
                    }
                });
            } else {
                // Swap back to player if server allows it
                ((net.minecraft.server.level.ServerLevel) context.getPlayer().level()).getServer().execute(() -> PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), null));
            }

            // Refresh player dimensions
            context.getPlayer().refreshDimensions();
        });
    }

    /**
     * Whether the second shape is being changed by the unlock key, after the server checked the player is
     * really near a mob of that type. Only then may friendly mobs be unlocked by it.
     */
    public static boolean isHandlingUnlockKey() {
        return handlingUnlockKey;
    }

    /**
     * Finds the mob the player unlocks: a living entity of the type within reach. Prefers one with the
     * requested variant, otherwise uses the variant of the closest one. Null when there's none.
     */
    private static @Nullable ShapeType<? extends LivingEntity> findTarget(ServerPlayer player, EntityType<?> entityType, int variant) {
        double range = player.entityInteractionRange() + TARGET_RANGE_MARGIN;
        AABB area = player.getBoundingBox().inflate(range);
        List<Entity> candidates = player.level().getEntities(player, area, entity -> entity.getType() == entityType && entity instanceof LivingEntity && entity.isAlive() && player.isWithinEntityInteractionRange(entity, TARGET_RANGE_MARGIN));
        ShapeType<? extends LivingEntity> closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (Entity candidate : candidates) {
            ShapeType<? extends LivingEntity> candidateType = ShapeType.from((LivingEntity) candidate);
            if (candidateType == null) {
                continue;
            }
            if (candidateType.getVariantData() == variant) {
                return candidateType;
            }
            double distance = candidate.distanceToSqr(player);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = candidateType;
            }
        }
        return closest;
    }

    /**
     * Server synchronizes the unlocked shape with the client
     */
    public static void sendSyncPacket(ServerPlayer player) {
        // Serialize unlocked to tag
        CompoundTag compound = new CompoundTag();
        CompoundTag id = new CompoundTag();
        ShapeType<?> ndShape = ((PlayerDataProvider) player).shape$get2ndShape();
        if (ndShape != null) {
            id = ndShape.writeCompound();
        }
        compound.put(UNLOCK_KEY, id);

        // Send to client
        ModernNetworking.sendToPlayer(player, NetworkHandler.UNLOCK_SYNC, compound);
    }

    /**
     * Client requests the server to unlock a shape
     */
    public static void sendUnlockRequest(@Nullable ShapeType<? extends LivingEntity> type) {
        CompoundTag packet = new CompoundTag();

        packet.putBoolean("valid_type", type != null);
        if (type != null) {
            packet.putString("type_id", EntityType.getKey(type.getEntityType()).toString());
            packet.putInt("variant", type.getVariantData());
        }

        ModernNetworking.sendToServer(ClientNetworking.UNLOCK_REQUEST, packet);
    }
}
