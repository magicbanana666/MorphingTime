package com.magicbanana666.morphingtime.collection.network;

import com.mojang.authlib.GameProfile;
import com.magicbanana666.morphingtime.core.network.ModernNetworking;
import com.magicbanana666.morphingtime.collection.MorphCollection;
import com.magicbanana666.morphingtime.collection.compatibility.SkinAccess;
import com.magicbanana666.morphingtime.collection.impl.PlayerMorph;
import com.magicbanana666.morphingtime.collection.permission.PermissionManager;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.api.PlayerShapeChanger;
import com.magicbanana666.morphingtime.shape.api.blacklist.EntityBlacklist;
import com.magicbanana666.morphingtime.shape.api.platform.ApiLevel;
import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import com.magicbanana666.morphingtime.shape.api.variant.TypeProvider;
import com.magicbanana666.morphingtime.shape.api.variant.TypeProviderRegistry;
import com.magicbanana666.morphingtime.shape.impl.PlayerDataProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

// TODO: Handle morphing through the regular shape packet instead of a separate morph request
public class NetworkHandler {
    public static final Identifier MORPH_REQUEST = MorphCollection.id("morph_request");
    public static final Identifier UNLOCKED_SYNC = MorphCollection.id("unlocked_sync");
    public static final Identifier FAVORITE_SYNC = MorphCollection.id("favorite_sync");
    public static final Identifier FAVORITE_UPDATE = MorphCollection.id("favorite_update");
    public static final Identifier RESET_SKIN = MorphCollection.id("reset_skin");
    public static final Identifier PERMISSION_CHECK = MorphCollection.id("permission_check");
    public static final Identifier PERMISSION_RESPONSE = MorphCollection.id("permission_response");
    public static final Identifier DELETE_SHAPE = MorphCollection.id("delete_shape");
    public static final Identifier DEFAULT_VARIANT_SYNC = MorphCollection.id("default_variant_sync");
    private static final int MAX_FAVORITE_SKINS = 256;

    public static void registerPacketReceiver() {
        ModernNetworking.registerReceiver(ModernNetworking.Side.C2S, NetworkHandler.MORPH_REQUEST, NetworkHandler::handleMorphRequestPacket);
        ModernNetworking.registerReceiver(ModernNetworking.Side.C2S, FAVORITE_UPDATE, NetworkHandler::handleFavoriteRequestPacket);
        ModernNetworking.registerReceiver(ModernNetworking.Side.C2S, NetworkHandler.RESET_SKIN, NetworkHandler::handleResetSkinPacket);
        ModernNetworking.registerReceiver(ModernNetworking.Side.C2S, PERMISSION_CHECK, NetworkHandler::handlePermissionCheckPacket);
        ModernNetworking.registerReceiver(ModernNetworking.Side.C2S, DELETE_SHAPE, NetworkHandler::handleDeleteShapePacket);

        ModernNetworking.registerType(UNLOCKED_SYNC);
        ModernNetworking.registerType(FAVORITE_SYNC);
        ModernNetworking.registerType(PERMISSION_RESPONSE);
        ModernNetworking.registerType(DEFAULT_VARIANT_SYNC);
    }

    private static void handleDeleteShapePacket(ModernNetworking.Context context, CompoundTag data) {
        boolean is_entity = data.getBoolean("is_entity").orElse(true);
        if (is_entity) {
            String id = data.getString("id").orElseThrow();
            @SuppressWarnings("unchecked") EntityType<@NotNull LivingEntity> type = (EntityType<@NotNull LivingEntity>) BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(id));
            PlayerMorph.handleSwap(context.getPlayer(), type);
        } else {
            String uuid = data.getString("uuid").orElseThrow();
            PlayerMorph.handleSwap(context.getPlayer(), UUID.fromString(uuid));
        }
    }

    public static void sendDeleteShapePacket(EntityType<?> type) {
        CompoundTag compound = new CompoundTag();
        compound.putBoolean("is_entity", true);
        compound.putString("id", EntityType.getKey(type).toString());

        ModernNetworking.sendToServer(NetworkHandler.DELETE_SHAPE, compound);
    }

    public static void sendDeleteShapePacket(UUID uuid) {
        CompoundTag compound = new CompoundTag();
        compound.putBoolean("is_entity", false);
        compound.putString("uuid", uuid.toString());

        ModernNetworking.sendToServer(NetworkHandler.DELETE_SHAPE, compound);
    }

    private static void handleResetSkinPacket(ModernNetworking.Context context, CompoundTag data) {
        if (MorphCollection.skinsEnabled) {
            SkinAccess.setSkin((ServerPlayer) context.getPlayer(), null);
        }
    }

    private static void handlePermissionCheckPacket(ModernNetworking.@NotNull Context context, @NotNull CompoundTag tag) {
        ServerPlayer player = (ServerPlayer) context.getPlayer();
        String permission = tag.getString("permission").orElse("");

        // Only check permissions if permissions are enabled
        boolean hasPermission = !MorphCollection.CONFIG.usePermissions || PermissionManager.hasPermission(player, permission);

        // Send response back to client
        CompoundTag response = new CompoundTag();
        response.putString("permission", permission);
        response.putBoolean("hasPermission", hasPermission);
        ModernNetworking.sendToPlayer(player, PERMISSION_RESPONSE, response);
    }

    public static void sendResetSkinPacket() {
        ModernNetworking.sendToServer(RESET_SKIN, new CompoundTag());
    }


    public static <T extends LivingEntity> void sendSwap2ndShapeRequest(@NotNull ShapeType<T> type) {
        CompoundTag compound = new CompoundTag();
        compound.putString("id", EntityType.getKey(type.getEntityType()).toString());
        compound.putInt("variant", type.getVariantData());

        ModernNetworking.sendToServer(NetworkHandler.MORPH_REQUEST, compound);
    }

    public static <T extends LivingEntity> void sendSwapSkinRequest(@NotNull GameProfile playerProfile) {
        CompoundTag compound = new CompoundTag();
        compound.putIntArray("playerUUID", UUIDUtil.uuidToIntArray(playerProfile.id()));

        ModernNetworking.sendToServer(NetworkHandler.MORPH_REQUEST, compound);
    }

    @SuppressWarnings({"unchecked"})
    private static void handleMorphRequestPacket(ModernNetworking.@NotNull Context context, CompoundTag compound) {
        ((ServerPlayer) context.getPlayer()).level().getServer().execute(() -> {
            // check if player is blacklisted
            if (Shapes.isPlayerBlacklisted(context.getPlayer().getUUID()) && Shapes.CONFIG.blacklistPreventsMorphing) {
                context.getPlayer().sendSystemMessage(Component.translatable("walkers.player_blacklisted"));
                return;
            }

            ServerPlayer player = (ServerPlayer) context.getPlayer();
            if (!ApiLevel.getCurrentLevel().canMorph) {
                return;
            }

            if (compound.contains("playerUUID") && MorphCollection.skinsEnabled) {
                UUID targetSkinUUID = UUIDUtil.uuidFromIntArray(compound.getIntArray("playerUUID").orElseThrow());
                // Only unlocked skins can be used, and the transform lock and permissions apply like for mobs
                if (!MorphCollection.canUseShape(player, null) || !MorphCollection.canUseSkin(player, targetSkinUUID)) {
                    return;
                }
                if (!Objects.equals(targetSkinUUID, SkinAccess.getCurrentSkin(player))) {
                    MorphCollection.spawnWalkersParticles(player);
                }
                SkinAccess.setSkin(player, targetSkinUUID);
                PlayerMorph.handleSwap(player, targetSkinUUID);
            } else {
                Identifier typeId = Identifier.tryParse(compound.getString("id").orElse(""));
                int typeVariant = compound.getIntOr("variant", -1);

                EntityType<? extends @NotNull LivingEntity> eType = typeId == null ? null : (EntityType<? extends @NotNull LivingEntity>) BuiltInRegistries.ENTITY_TYPE.get(typeId).map(Holder::value).orElse(null);
                // Unknown and non-living types can't be shapes
                if (eType == null || !MorphCollection.isLivingType(player.level(), eType) || EntityBlacklist.isBlacklisted(eType)) {
                    return;
                }
                if (!isSelectableVariant(player, eType, typeVariant)) {
                    return;
                }

                @Nullable
                ShapeType<? extends LivingEntity> type = ShapeType.from(eType, typeVariant);
                if (type == null) {
                    return;
                }
                // Update the player
                boolean result = PlayerShapeChanger.change2ndShape(player, type);
                if (result) {
                    PlayerShape.updateShapes(player, type.create(player.level(), player));
                }

                // Refresh player dimensions
                player.refreshDimensions();
            }
        });
    }

    /**
     * Whether the menu could have sent this variant: the default one, the one the player is in or picked
     * before, or any variant when variant selection is allowed.
     */
    private static boolean isSelectableVariant(ServerPlayer player, EntityType<? extends LivingEntity> type, int variant) {
        if (variant == -1 || variant == ShapeType.getDefaultVariantData(type)) {
            return true;
        }
        TypeProvider<?> provider = TypeProviderRegistry.getProvider(type);
        if (provider == null) {
            return false;
        }
        if (variant < 0 || variant >= provider.size(player.level())) {
            return false;
        }
        if (Shapes.CONFIG.unlockEveryVariant) {
            return true;
        }
        ShapeType<?> current = ShapeType.from(PlayerShape.getCurrentShape(player));
        ShapeType<?> second = ((PlayerDataProvider) player).shape$get2ndShape();
        ShapeType<?> remembered = PlayerMorph.getDefaultVariants(player).get(type);
        return isVariant(current, type, variant) || isVariant(second, type, variant) || isVariant(remembered, type, variant);
    }

    private static boolean isVariant(@Nullable ShapeType<?> shape, EntityType<?> type, int variant) {
        return shape != null && shape.getEntityType() == type && shape.getVariantData() == variant;
    }

    public static void sendFavoriteSync(ServerPlayer player) {
        Set<EntityType<?>> favoriteShapes = PlayerMorph.getFavoriteShapes(player);
        Set<UUID> favoriteSkins = PlayerMorph.getFavoriteSkinIds(player);
        CompoundTag tag = new CompoundTag();
        ListTag shapeIdList = new ListTag();
        ListTag skinIdList = new ListTag();
        favoriteShapes.forEach(type -> shapeIdList.add(StringTag.valueOf(EntityType.getKey(type).toString())));
        favoriteSkins.forEach(skin -> skinIdList.add(new IntArrayTag(UUIDUtil.uuidToIntArray(skin))));
        tag.put("FavoriteShapes", shapeIdList);
        tag.put("FavoriteSkins", skinIdList);

        // Create & send packet with NBT
        ModernNetworking.sendToPlayer(player, NetworkHandler.FAVORITE_SYNC, tag);
    }

    public static void sendFavoriteRequest(@NotNull EntityType<? extends @NotNull LivingEntity> type, boolean favorite) {
        CompoundTag packet = new CompoundTag();
        packet.putString("id", EntityType.getKey(type).toString());
        packet.putBoolean("favorite", favorite);
        ModernNetworking.sendToServer(FAVORITE_UPDATE, packet);
    }

    public static void sendFavoriteRequest(@NotNull GameProfile playerProfile, boolean favorite) {
        CompoundTag packet = new CompoundTag();
        packet.putIntArray("playerUUID", UUIDUtil.uuidToIntArray(playerProfile.id()));
        packet.putBoolean("favorite", favorite);
        ModernNetworking.sendToServer(FAVORITE_UPDATE, packet);
    }

    @SuppressWarnings({"unchecked"})
    private static void handleFavoriteRequestPacket(ModernNetworking.Context context, @NotNull CompoundTag packet) {
        boolean favorite = packet.getBooleanOr("favorite", false);

        if (packet.contains("playerUUID")) {
            UUID skinId = UUIDUtil.uuidFromIntArray(packet.getIntArray("playerUUID").orElseThrow());

            ((ServerPlayer) context.getPlayer()).level().getServer().execute(() -> {
                if (favorite) {
                    // Only unlocked skins can be favorites, and the list is capped so it can't grow forever
                    Set<UUID> favorites = PlayerMorph.getFavoriteSkinIds(context.getPlayer());
                    if (MorphCollection.canUseSkin(context.getPlayer(), skinId) && favorites.size() < MAX_FAVORITE_SKINS) {
                        favorites.add(skinId);
                    }
                } else {
                    PlayerMorph.getFavoriteSkinIds(context.getPlayer()).remove(skinId);
                }
                // re-sync favorites
                sendFavoriteSync((ServerPlayer) context.getPlayer());
            });
        } else {
            EntityType<? extends @NotNull LivingEntity> entityType = (EntityType<? extends @NotNull LivingEntity>) BuiltInRegistries.ENTITY_TYPE.get(Identifier.parse(packet.getString("id").orElseThrow())).map(Holder::value).orElse(null);

            if (entityType != null) {
                ((ServerPlayer) context.getPlayer()).level().getServer().execute(() -> {
                    if (favorite) {
                        PlayerMorph.getFavoriteShapes(context.getPlayer()).add(entityType);
                    } else {
                        PlayerMorph.getFavoriteShapes(context.getPlayer()).remove(entityType);
                    }
                    // resync favorites
                    sendFavoriteSync((ServerPlayer) context.getPlayer());
                });
            }
        }
    }

    public static void sendDefaultVariantSync(ServerPlayer player) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        PlayerMorph.getDefaultVariants(player).forEach((type, shape) -> {
            CompoundTag e = new CompoundTag();
            e.putString("entity_id", EntityType.getKey(type).toString());
            e.putInt("variant", shape.getVariantData());
            list.add(e);
        });
        tag.put("DefaultVariants", list);
        ModernNetworking.sendToPlayer(player, DEFAULT_VARIANT_SYNC, tag);
    }
}
