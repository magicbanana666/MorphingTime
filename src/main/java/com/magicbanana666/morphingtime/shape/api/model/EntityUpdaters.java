package com.magicbanana666.morphingtime.shape.api.model;

import com.magicbanana666.morphingtime.shape.api.model.impl.AbstractHorseEntityUpdater;
import com.magicbanana666.morphingtime.shape.api.model.impl.ShulkerEntityUpdater;
import com.magicbanana666.morphingtime.shape.api.model.impl.SquidEntityUpdater;
import com.magicbanana666.morphingtime.shape.impl.NearbySongAccessor;
import com.magicbanana666.morphingtime.shape.impl.PlayerDataProvider;
import com.magicbanana666.morphingtime.shape.mixin.accessor.*;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry class for {@link EntityUpdater} instances.
 *
 * <p>
 * {@link EntityUpdater}s are used to apply changes to shape entity instances on
 * the client using information from the player. As an example, an
 * {@link EntityUpdater} can be used to tell a shape bat to "stop roosting,"
 * which triggers the flight animation. {@link EntityUpdater}s are called once
 * every render tick
 * {@link net.minecraft.client.renderer.entity.EntityRenderer#extractRenderState(Entity, EntityRenderState, float)}
 */
@SuppressWarnings("resource")

public class EntityUpdaters {
    private static final Map<EntityType<? extends @NotNull LivingEntity>, EntityUpdater<? extends LivingEntity>> map = new LinkedHashMap<>();

    /**
     * Returns a {@link EntityUpdater} if one has been registered for the given
     * {@link EntityType}, or null.
     *
     * @param entityType entity type key to retrieve a value registered in
     *                   {@link EntityUpdaters#register(EntityType, EntityUpdater)}
     * @param <T>        passed in {@link EntityType} generic
     * @return registered {@link EntityUpdater} instance for the given
     * {@link EntityType}, or null if one does not exist
     */
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity> EntityUpdater<T> getUpdater(EntityType<@NotNull T> entityType) {
        return (EntityUpdater<T>) map.getOrDefault(entityType, null);
    }

    /**
     * Registers an {@link EntityUpdater} for the given {@link EntityType}.
     *
     * <p>
     * Note that a given {@link EntityType} can only have 1 {@link EntityUpdater}
     * associated with it.
     *
     * @param type          entity type key associated with the given
     *                      {@link EntityUpdater}
     * @param entityUpdater {@link EntityUpdater} associated with the given
     *                      {@link EntityType}
     * @param <T>           passed in {@link EntityType} generic
     */
    public static <T extends LivingEntity> void register(EntityType<@NotNull T> type, EntityUpdater<T> entityUpdater) {
        map.put(type, entityUpdater);
    }

    public static void init() {
        EntityUpdaters.register(EntityTypes.HORSE, new AbstractHorseEntityUpdater<>());
        EntityUpdaters.register(EntityTypes.DONKEY, new AbstractHorseEntityUpdater<>());
        EntityUpdaters.register(EntityTypes.MULE, new AbstractHorseEntityUpdater<>());

        EntityUpdaters.register(EntityTypes.ALLAY, (player, allay) -> {
            ((AllayAccessor) allay).setHoldingItemAnimationTicks0(((AllayAccessor) allay).getHoldingItemAnimationTicks());
            if (allay.hasItemInHand()) {
                ((AllayAccessor) allay).setHoldingItemAnimationTicks(Mth.clamp(((AllayAccessor) allay).getHoldingItemAnimationTicks() + 1.0F, 0.0F, 5.0F));
            } else {
                ((AllayAccessor) allay).setHoldingItemAnimationTicks(Mth.clamp(((AllayAccessor) allay).getHoldingItemAnimationTicks() - 1.0F, 0.0F, 5.0F));
            }
        });

        // register specific entity animation handling
        EntityUpdaters.register(EntityTypes.BAT, (player, bat) -> {
            bat.tickCount = player.tickCount;
            bat.setResting(!player.level().getBlockState(player.blockPosition().above()).isAir());
            ((BatAccessor) bat).callSetupAnimationStates();
        });

        EntityUpdaters.register(EntityTypes.PARROT, (player, parrot) -> {
            parrot.setRecordPlayingNearby(player.blockPosition(), ((NearbySongAccessor) player).shape_isNearbySongPlaying());
            ((ParrotEntityAccessor) parrot).callCalculateFlapping();
            // imitate sounds
            if (player.getRandom().nextInt(400) == 0) {
                Parrot.imitateNearbyMobs(player.level(), player);
            }
        });

        EntityUpdaters.register(EntityTypes.ENDERMAN, (player, enderman) -> {
            ItemStack heldStack = player.getMainHandItem();

            if (heldStack.getItem() instanceof BlockItem) {
                enderman.setCarriedBlock(((BlockItem) heldStack.getItem()).getBlock().defaultBlockState());
            }
        });

        // To prevent creeper shapes from flickering white, reset currentFuseTime to 0.
        // Creepers normally tick their fuse timer in tick(), but:
        // 1. shapes do not tick
        // 2. the creeper ability is instant, so ticking does not need to be re-implemented
        EntityUpdaters.register(EntityTypes.CREEPER, (player, creeper) -> ((CreeperEntityAccessor) creeper).setSwell(0));

        EntityUpdaters.register(EntityTypes.SQUID, new SquidEntityUpdater<>());
        EntityUpdaters.register(EntityTypes.GLOW_SQUID, new SquidEntityUpdater<>());

        EntityUpdaters.register(EntityTypes.SHULKER, new ShulkerEntityUpdater());

        EntityUpdaters.register(EntityTypes.CHICKEN, (player, chicken) -> {
            chicken.oFlap = chicken.flap;
            chicken.oFlapSpeed = chicken.flapSpeed;
            chicken.flapSpeed += (player.onGround() ? -1.0F : 4.0F) * 0.3F;
            chicken.flapSpeed = Mth.clamp(chicken.flapSpeed, 0.0F, 1.0F);
            if (!player.onGround() && chicken.flapping < 1.0F) {
                chicken.flapping = 1.0F;
            }
            chicken.flapping *= 0.9F;
            chicken.flap += chicken.flapping * 2.0F;
        });

        // make striders shake and turn purple when out of lava
        EntityUpdaters.register(EntityTypes.STRIDER, (player, strider) -> {
            BlockState blockState = player.level().getBlockState(player.blockPosition());
            boolean bl = blockState.is(BlockTags.STRIDER_WARM_BLOCKS) || player.getFluidHeight(FluidTags.LAVA) > 0.0;
            strider.setSuffocating(!bl);
        });

        EntityUpdaters.register(EntityTypes.CAT, (player, cat) -> cat.setInSittingPose(((PlayerDataProvider) player).shape$getIsSpecialAnim()));

        EntityUpdaters.register(EntityTypes.HOGLIN, (player, hoglin) -> hoglin.setImmuneToZombification(!player.level().environmentAttributes().getValue(EnvironmentAttributes.PIGLINS_ZOMBIFY, player.position())));

        EntityUpdaters.register(EntityTypes.PIGLIN, (player, piglin) -> {
            piglin.setImmuneToZombification(!player.level().environmentAttributes().getValue(EnvironmentAttributes.PIGLINS_ZOMBIFY, player.position()));
            piglin.setDancing(((PlayerDataProvider) player).shape$getIsSpecialAnim());
        });

        EntityUpdaters.register(EntityTypes.PIGLIN_BRUTE, (player, piglinBrute) -> piglinBrute.setImmuneToZombification(!player.level().environmentAttributes().getValue(EnvironmentAttributes.PIGLINS_ZOMBIFY, player.position())));

        EntityUpdaters.register(EntityTypes.PANDA, (player, panda) -> {
            panda.sit(((PlayerDataProvider) player).shape$getIsSpecialAnim());
            ((PandaAccessor) panda).callUpdateSitAmount();
        });

        EntityUpdaters.register(EntityTypes.VEX, (player, vex) -> vex.setIsCharging(((PlayerDataProvider) player).shape$getIsSpecialAnim()));

    }
}
