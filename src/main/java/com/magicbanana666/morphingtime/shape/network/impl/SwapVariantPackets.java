package com.magicbanana666.morphingtime.shape.network.impl;

import com.magicbanana666.morphingtime.core.network.ModernNetworking;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.api.PlayerShapeChanger;
import com.magicbanana666.morphingtime.shape.api.platform.ApiLevel;
import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import com.magicbanana666.morphingtime.shape.api.variant.TypeProvider;
import com.magicbanana666.morphingtime.shape.api.variant.TypeProviderRegistry;
import com.magicbanana666.morphingtime.shape.network.ClientNetworking;
import com.magicbanana666.morphingtime.shape.network.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class SwapVariantPackets {

    @SuppressWarnings("ConstantConditions")
    public static void registerSwapVariantPacketHandler() {
        ModernNetworking.registerReceiver(ModernNetworking.Side.C2S, NetworkHandler.VARIANT_REQUEST,
                (context, packet) -> {
                    if (!ApiLevel.getCurrentLevel().allowVariantsMenu) {
                        return;
                    }

                    int variantID = packet.getInt("variant_id").orElse(-1);
                    ((net.minecraft.server.level.ServerLevel) context.getPlayer().level()).getServer().execute(() -> {
                        if (Shapes.CONFIG.unlockEveryVariant) {
                            ShapeType<?> currentShapeType = ShapeType.from(PlayerShape.getCurrentShape(context.getPlayer()));
                            // Players without a shape have no variants to swap to
                            if (currentShapeType == null) {
                                return;
                            }

                            TypeProvider<?> typeProvider = TypeProviderRegistry.getProvider(currentShapeType.getEntityType());
                            int range = typeProvider != null ? typeProvider.size(context.getPlayer().level()) : -1;

                            // switch to special shape
                            if (Shapes.hasSpecialShape(context.getPlayer().getUUID()) && EntityType.getKey(currentShapeType.getEntityType()).equals(Identifier.parse("minecraft:wolf")) && variantID == range) {
                                Entity created;
                                CompoundTag nbt = new CompoundTag();

                                nbt.putBoolean("isSpecial", true);
                                nbt.putString("id", EntityType.getKey(currentShapeType.getEntityType()).toString());
                                created = EntityType.loadEntityRecursive(nbt, context.getPlayer().level(), new EntitySpawnRequest(EntitySpawnReason.LOAD, false), it -> it);
                                PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), (LivingEntity) created);
                            }
                            // switch normally
                            else {
                                // DO NOT check if variant is already equipped; some variants are biome based!
                                if (currentShapeType != null) {
                                    ShapeType<?> newShapeType = ShapeType.from(currentShapeType.getEntityType(), variantID);
                                    if (newShapeType != null) {
                                        if (PlayerShapeChanger.change2ndShape((ServerPlayer) context.getPlayer(), newShapeType) || !ApiLevel.getCurrentLevel().canUnlock) {
                                            LivingEntity shape = newShapeType.create(context.getPlayer().level(), context.getPlayer());
                                            if (shape != null) {
                                                PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), shape);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                });
    }

    public static void sendSwapRequest(int variantID) {
        if (Shapes.CONFIG.unlockEveryVariant) {
            if (!ApiLevel.getCurrentLevel().allowVariantsMenu) {
                return;
            }

            CompoundTag packet = new CompoundTag();
            packet.putInt("variant_id", variantID);
            ModernNetworking.sendToServer(ClientNetworking.VARIANT_REQUEST, packet);
        }
    }
}
