package com.magicbanana666.morphingtime.collection;

import com.mojang.authlib.GameProfile;
import com.magicbanana666.morphingtime.core.config.ConfigLoader;
import com.magicbanana666.morphingtime.core.event.common.CommandEvents;
import com.magicbanana666.morphingtime.core.event.common.EntityEvents;
import com.magicbanana666.morphingtime.core.event.common.PlayerEvents;
import com.magicbanana666.morphingtime.core.network.ModernNetworking;
import com.magicbanana666.morphingtime.core.platform.PlatformData;
import com.magicbanana666.morphingtime.collection.command.CollectionCommand;
import com.magicbanana666.morphingtime.collection.config.UnlockConfig;
import com.magicbanana666.morphingtime.collection.handler.LivingDeathHandler;
import com.magicbanana666.morphingtime.collection.handler.PlayerRespawnHandler;
import com.magicbanana666.morphingtime.collection.handler.SwapShapeCallback;
import com.magicbanana666.morphingtime.collection.handler.UnlockShapeCallback;
import com.magicbanana666.morphingtime.collection.impl.PlayerMorph;
import com.magicbanana666.morphingtime.collection.network.NetworkHandler;
import com.magicbanana666.morphingtime.collection.permission.PermissionManager;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.api.events.ShapeEvents;
import com.magicbanana666.morphingtime.shape.api.platform.ApiLevel;
import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@SuppressWarnings("resource")
public class MorphCollection {
    @ApiStatus.Internal
    public static final Logger LOGGER = LoggerFactory.getLogger(MorphCollection.class);
    public static final String MODID = "remorphed";
    public static final UnlockConfig CONFIG = ConfigLoader.register(MODID);
    public static boolean displayDataInMenu = CONFIG.show_traits_by_default;
    @ApiStatus.Internal
    public static final boolean skinsEnabled = true;

    public void initialize() {
        Shapes.CONFIG.unlockEveryVariant = CONFIG.allow_variant_selection;

        ShapeEvents.UNLOCK_SHAPE.register(new UnlockShapeCallback());
        ShapeEvents.SWAP_SHAPE.register(new SwapShapeCallback());
        if (!CONFIG.unlockFriendlyNormal) {
            if (CONFIG.allow_variant_selection) {
                ApiLevel.setApiLevel(ApiLevel.MORPHING_AND_VARIANTS_MENU_ONLY);
            } else {
                ApiLevel.setApiLevel(ApiLevel.MORPHING_ONLY);
            }
        }
        // With friendly mob unlocking on, keep the full api level so the unlock key works. Variant
        // selection is still controlled by allow_variant_selection through unlockEveryVariant above

        // Grant the special shape to DarkShadow_2k (creator of the special shape icon and concepts)
        //noinspection UnstableApiUsage
        Shapes.devs.add(UUID.fromString("74b6d9b3-c8c1-40db-ab82-ccc290d1aa03"));

        // Grant the special shape to LugFong (FugLong) (added permissions and updated menu rendering)
        //noinspection UnstableApiUsage
        Shapes.devs.add(UUID.fromString("d014c6c7-1c15-46b7-94e1-a5dd28f9425e"));


        if (PlatformData.getEnv() == PlatformData.Env.CLIENT) new MorphCollectionClient().initialize();

        NetworkHandler.registerPacketReceiver();

        CommandEvents.REGISTRATION.register(new CollectionCommand());
        EntityEvents.LIVING_DEATH.register(new LivingDeathHandler());
        PlayerEvents.PLAYER_RESPAWN.register(new PlayerRespawnHandler());

        // Let the unlock key replace the current shape when friendly mob unlocking is enabled
        Shapes.CONFIG.unlockOverridesCurrentShape = MorphCollection.CONFIG.unlockFriendlyNormal;
        Shapes.CONFIG.save();

        // Sync favorites
        PlayerEvents.PLAYER_JOIN.register(NetworkHandler::sendFavoriteSync);
    }

    public static boolean canUseEveryShape(@NotNull Player player) {
        // Creative players can use every shape if the config allows it
        return player.isCreative() && CONFIG.creativeUnlockAll;
    }

    public static boolean canUseShape(Player player, EntityType<?> type) {
        // If permissions are enabled, use permission-based logic
        if (CONFIG.usePermissions && player instanceof ServerPlayer serverPlayer) {
            // Check basic morph permission
            if (!PermissionManager.canMorph(serverPlayer)) {
                return false;
            }

            // Check entity type specific permission - this takes precedence over creative permissions
            if (type != null && !PermissionManager.canMorphIntoType(serverPlayer, type)) {
                return false;
            }

            // Check bypass transform lock permission
            if (MorphCollection.CONFIG.lockTransform && !PermissionManager.canBypassTransformLock(serverPlayer)) {
                return false;
            }

            // Check if player can use all shapes (config-based creative logic)
            if (canUseEveryShape(player)) {
                return true;
            }

            // For permission-enabled players without creative permission, check kill requirements
            return !MorphCollection.CONFIG.lockTransform && (type == null || MorphCollection.getKillToUnlock(player, type) <= 0 || PlayerMorph.getKills(player, type) >= MorphCollection.getKillToUnlock(player, type));
        }

        // If permissions are disabled, use the config-based behavior
        return canUseEveryShape(player) || !MorphCollection.CONFIG.lockTransform && (type == null || MorphCollection.getKillToUnlock(player, type) <= 0 || PlayerMorph.getKills(player, type) >= MorphCollection.getKillToUnlock(player, type));
    }

    public static List<EntityType<?>> getUnlockedShapes(Player player) {
        // Always filter by canUseShape to respect entity type permissions
        List<EntityType<?>> unlocked = new ArrayList<>();
        for (EntityType<?> shapeType : getAllTypes(player.level())) {
            if (!unlocked.contains(shapeType) && canUseShape(player, shapeType)) {
                unlocked.add(shapeType);
            }
        }
        return unlocked;
    }

    /** Whether the entity type is a living entity, so it can be used as a shape. */
    public static boolean isLivingType(Level world, EntityType<?> type) {
        return type != null && getAllTypes(world).contains(type);
    }

    private static final List<EntityType<?>> LIVING_TYPE_CASH = new ArrayList<>();

    private static @NotNull List<EntityType<?>> getAllTypes(Level world) {
        if (LIVING_TYPE_CASH.isEmpty()) {
            for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
                try {
                    Entity instance = type.create(world, EntitySpawnReason.LOAD);
                    if (instance instanceof LivingEntity) {
                        LIVING_TYPE_CASH.add(type);
                    }
                } catch (Exception e) {
                    MorphCollection.LOGGER.error("{}: Caught an exception while getting shape types for entity type + {}: {}", ShapeType.class.getSimpleName(), type.toShortString(), e);
                }
            }
        }

        return LIVING_TYPE_CASH;
    }

    @Contract("_ -> new")
    public static @NotNull List<GameProfile> getUnlockedSkins(Player player) {
        int killRequirement = getPlayerKillRequirement(player);
        return new ArrayList<>(PlayerMorph.getUnlockedSkinIds(player).keySet().stream().filter(skinId -> (PlayerMorph.getPlayerKills(player, skinId) >= killRequirement || killRequirement == 0) && killRequirement != -1).map(id -> getGameProfile(player, id)).filter(Optional::isPresent).map(Optional::get).toList());
    }

    /**
     * Whether the player has unlocked the skin. Same rules as {@link #getUnlockedSkins(Player)}, but without
     * looking up the game profile, so it's cheap enough for checking packets on the server.
     */
    public static boolean canUseSkin(Player player, UUID skinId) {
        int killRequirement = getPlayerKillRequirement(player);
        return killRequirement != -1 && PlayerMorph.getUnlockedSkinIds(player).containsKey(skinId) && (killRequirement == 0 || PlayerMorph.getPlayerKills(player, skinId) >= killRequirement);
    }

    public static int getKillToUnlock(EntityType<?> type) {
        return MorphCollection.CONFIG.killToUnlockByType.getOrDefault(EntityType.getKey(type).toString(), MorphCollection.CONFIG.killToUnlock);
    }

    public static int getKillToUnlock(Player player, EntityType<?> type) {
        return getKillToUnlock(type);
    }

    public static int getKillValue(EntityType<?> type) {
        return MorphCollection.CONFIG.killValueByType.getOrDefault(EntityType.getKey(type).toString(), MorphCollection.CONFIG.killValue);
    }

    public static int getPlayerKillRequirement(Player player) {
        return CONFIG.killToUnlockPlayers;
    }

    public static void sync(ServerPlayer player) {
        sync(player, player);
    }

    public static void sync(ServerPlayer changed, ServerPlayer packetTarget) {
        CompoundTag compoundTag = new CompoundTag();

        // serialize current shape data to tag if it exists
        Map<EntityType<?>, Integer> unlockedShapes = PlayerMorph.getUnlockedShapes(changed);
        ListTag shapesList = new ListTag();
        unlockedShapes.forEach((shape, killAmount) -> {
            if (killAmount > 0 && shape != null) {
                CompoundTag compound = new CompoundTag();
                compound.putString("id", EntityType.getKey(shape).toString());
                compound.putInt("killAmount", killAmount);
                shapesList.add(compound);
            }
        });
        if (!shapesList.isEmpty()) compoundTag.put("UnlockedShapes", shapesList);

        Map<UUID, Integer> unlockedSkins = PlayerMorph.getUnlockedSkinIds(changed);
        ListTag skinsList = new ListTag();
        unlockedSkins.forEach((skinId, killAmount) -> {
            if (killAmount > 0 && skinId != null) {
                CompoundTag compound = new CompoundTag();
                compound.putIntArray("uuid", UUIDUtil.uuidToIntArray(skinId));
                compound.putInt("killAmount", killAmount);
                skinsList.add(compound);
            }
        });
        if (!skinsList.isEmpty()) compoundTag.put("UnlockedSkins", skinsList);

        ListTag morphCounter = new ListTag();
        PlayerMorph.getShapeCounter(changed).forEach((type, count) -> {
            if (count > 0 && type != null) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putBoolean("isSkin", false);
                entryTag.putString("id", EntityType.getKey(type).toString());
                entryTag.putInt("counter", count);
                morphCounter.add(entryTag);
            }
        });
        PlayerMorph.getSkinCounter(changed).forEach((skinId, count) -> {
            if (count > 0 && skinId != null) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putBoolean("isSkin", true);
                entryTag.putIntArray("uuid", UUIDUtil.uuidToIntArray(skinId));
                entryTag.putInt("counter", count);
                morphCounter.add(entryTag);
            }
        });
        if (!morphCounter.isEmpty()) {
            compoundTag.put("MorphCounter", morphCounter);
        }

        compoundTag.putIntArray("uuid", UUIDUtil.uuidToIntArray(changed.getUUID()));
        ModernNetworking.sendToPlayer(packetTarget, NetworkHandler.UNLOCKED_SYNC, compoundTag);
        NetworkHandler.sendDefaultVariantSync(packetTarget);
    }

    public static @NotNull Optional<GameProfile> getGameProfile(Player owner, UUID uuid) {
        // Get the ProfileResolver for the current side
        final ProfileResolver profileResolver = switch (PlatformData.getEnv()) {
            case CLIENT -> Minecraft.getInstance().services().profileResolver();
            case SERVER -> ((ServerPlayer) owner).level().getServer().services().profileResolver();
        };

        return profileResolver.fetchById(uuid);
    }

    @Contract("_ -> new")
    public static @NotNull Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MODID, name);
    }

    public static void spawnWalkersParticles(Player player) {
        // spawn particles
        if (Shapes.CONFIG.emit_particles && !player.isSpectator() && player.level() instanceof ServerLevel l) {
            l.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1, player.getZ(), 25, .5, 1.0, .5, .75);
            l.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1, player.getZ(), 25, .5, 1.0, .5, .75);
            l.sendParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getY() + 1, player.getZ(), 25, .5, 1.0, .5, .7);
        }
    }
}
