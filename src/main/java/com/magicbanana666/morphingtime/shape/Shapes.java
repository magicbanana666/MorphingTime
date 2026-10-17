package com.magicbanana666.morphingtime.shape;

import com.magicbanana666.morphingtime.core.config.ConfigLoader;
import com.magicbanana666.morphingtime.core.event.common.EntityEvents;
import com.magicbanana666.morphingtime.core.event.common.PlayerEvents;
import com.magicbanana666.morphingtime.core.event.common.ServerLevelEvents;
import com.magicbanana666.morphingtime.shape.ability.AbilityRegistry;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.api.ShapeTickHandlers;
import com.magicbanana666.morphingtime.shape.api.data.DataManager;
import com.magicbanana666.morphingtime.shape.api.platform.ApiLevel;
import com.magicbanana666.morphingtime.shape.api.platform.MorphConfig;
import com.magicbanana666.morphingtime.shape.command.ShapeCommand;
import com.magicbanana666.morphingtime.shape.eventhandler.LivingBreatheHandler;
import com.magicbanana666.morphingtime.shape.eventhandler.RespawnHandler;
import com.magicbanana666.morphingtime.shape.eventhandler.ShapeEventHandlers;
import com.magicbanana666.morphingtime.shape.integrations.Integrations;
import com.magicbanana666.morphingtime.shape.mixin.ThreadedAnvilChunkStorageAccessor;
import com.magicbanana666.morphingtime.shape.network.ServerNetworking;
import com.magicbanana666.morphingtime.shape.traits.TraitRegistry;
import com.magicbanana666.morphingtime.shape.traits.impl.FlyingTrait;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Shapes {
    public static ProblemReporter PROBLEM_REPORTER = new ProblemReporter() {
        @Contract(pure = true)
        @Override
        public @NotNull ProblemReporter forChild(@NotNull PathElement pathElement) {
            return this;
        }

        @Override
        public void report(@NotNull Problem problem) {
            Shapes.LOGGER.error(problem.description());
        }
    };
    public static final AttributeModifier WAYPOINT_TRANSMIT_MODIFIER = new AttributeModifier(Shapes.id("waypoint_transmit_disguised"), -1.0F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);;

    @ApiStatus.Internal
    public static final Logger LOGGER = LoggerFactory.getLogger(Shapes.class);
    public static final String MODID = "walkers";
    public static final MorphConfig CONFIG = ConfigLoader.register(MODID);
    @ApiStatus.Internal
    public static final List<UUID> devs = new ArrayList<>() {
        {
            add(UUID.fromString("1f63e38e-4059-4a4f-b7c4-0fac4a48e744"));
            add(UUID.fromString("494e1c8a-f733-43ed-8c1b-a2943fdc05f3"));
            add(UUID.fromString("eb83f5a3-397a-4e14-80bc-914ff91890f0"));
            add(UUID.fromString("17eae0aa-e445-489a-92ae-697d0a9d5731"));
            add(UUID.fromString("53430a02-dbd1-4b5c-9984-4119e38fec79"));
            add(UUID.fromString("50ee913f-a7d6-45ea-8d09-45fb3726aec1"));
            add(UUID.fromString("74b6d9b3-c8c1-40db-ab82-ccc290d1aa03"));
            add(UUID.fromString("d4a50582-c44e-4a0d-ab0c-9711e2cf4b29"));
            add(UUID.fromString("ccddb138-0b29-493f-9d27-0f51ed3a0578"));
            add(UUID.fromString("76754682-2435-4a2e-b2b5-c3ee99782812"));

        }
    };

    public void initialize() {
        Integrations.initIntegrations();

        TraitRegistry.initialize();
        AbilityRegistry.initialize();

        ShapeCommand.initialize();
        ShapeEventHandlers.initialize();
        ServerNetworking.initialize();
        registerJoinSyncPacket();
        ShapeTickHandlers.initialize();
        DataManager.initialize();
        Integrations.initialize();

        PlayerEvents.PLAYER_RESPAWN.register(new RespawnHandler());
        EntityEvents.LIVING_BREATHE.register(new LivingBreatheHandler());

        ServerLevelEvents.LEVEL_LOAD.register(level -> ApiLevel.readConfig());
    }

    public static void registerJoinSyncPacket() {

        PlayerEvents.PLAYER_JOIN.register(player -> {
            Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) player.level()
                    .getChunkSource().chunkMap).getEntityMap();
            trackers.forEach((entityid, tracking) -> {
                if (player.level().getEntity(entityid) instanceof ServerPlayer) {
                    PlayerShape.sync((ServerPlayer) player.level().getEntity(entityid), player);
                }
            });
        });
    }

    public static @NotNull Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MODID, name);
    }

    public static boolean hasFlyingPermissions(@NotNull ServerPlayer player) {
        return hasFlyingPermissions(player, player.gameMode.getGameModeForPlayer());
    }

    public static boolean hasFlyingPermissions(@NotNull ServerPlayer player, @NotNull GameType gameMode) {
        if (gameMode.isCreative()) {
            return true;
        }

        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (shape != null && Shapes.CONFIG.enableFlight
                && (TraitRegistry.has(shape, FlyingTrait.ID) || shape instanceof FlyingAnimal)) {
            List<String> requiredAdvancements = Shapes.CONFIG.advancementsRequiredForFlight;

            // requires at least 1 advancement, check if player has them
            if (!requiredAdvancements.isEmpty()) {

                boolean hasPermission = true;
                for (String requiredAdvancement : requiredAdvancements) {
                    AdvancementHolder advancement = player.level().getServer().getAdvancements().get(Identifier.parse(requiredAdvancement));
                    if (advancement != null) {
                        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);

                        if (!progress.isDone()) {
                            hasPermission = false;
                        }
                    }
                }

                return hasPermission;
            }

            return true;
        }

        return false;
    }

    public static boolean isPlayerBlacklisted(UUID uuid) {
        return CONFIG.playerBlacklistIsWhitelist != CONFIG.playerUUIDBlacklist.contains(uuid);
    }

    public static boolean hasSpecialShape(UUID uuid) {
        return devs.contains(uuid);
    }
}
