package com.magicbanana666.morphingtime.shape.mixin.client;

import com.magicbanana666.morphingtime.client.animation.MorphTransitions;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.magicbanana666.morphingtime.core.util.Maths;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.api.model.ArmRenderingManipulator;
import com.magicbanana666.morphingtime.shape.api.model.EntityArms;
import com.magicbanana666.morphingtime.shape.impl.ShapeAvatarRenderer;
import com.magicbanana666.morphingtime.shape.impl.ShapeRenderStateProvider;
import com.magicbanana666.morphingtime.shape.mixin.accessor.EntityAccessor;
import com.magicbanana666.morphingtime.shape.mixin.accessor.LivingEntityAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@SuppressWarnings({"rawtypes", "unchecked"})

@Mixin(value = AvatarRenderer.class, priority = 1001)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, AvatarRenderState, PlayerModel> implements ShapeAvatarRenderer {
    private PlayerEntityRendererMixin(EntityRendererProvider.Context ctx, PlayerModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("RETURN"))
    private void onCreateState(Avatar avatar, AvatarRenderState state, float f, CallbackInfo ci) {
        if (!(avatar instanceof AbstractClientPlayer player)) return;

        ((ShapeRenderStateProvider) state).shape$setInvisRide(Minecraft.getInstance().options.getCameraType().isFirstPerson() && player.getVehicle() == Minecraft.getInstance().getCameraEntity());

        LivingEntity shape = PlayerShape.getCurrentShape(player);
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() || player.getVehicle() != Minecraft.getInstance().getCameraEntity()) {
            if (shape != null) {
                ((ShapeRenderStateProvider) state).shape$setShapeIsTameable(shape instanceof TamableAnimal);
                ((ShapeRenderStateProvider) state).shape$setShapeRenderer(() -> {
                    shape$updateShapeAttributes(player, shape);
                    return (EntityRenderer<@NotNull LivingEntity, @NotNull EntityRenderState>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(shape);
                });
                ((ShapeRenderStateProvider) state).shape$setShapeRenderState(() -> {
                    EntityRenderer<@NotNull LivingEntity, @NotNull EntityRenderState> shapeRenderer = ((ShapeRenderStateProvider) state).shape$getShapeRenderer();
                    if (shapeRenderer != null) {
                        EntityRenderState shapeState = shapeRenderer.createRenderState(shape, 1.0f);
                        shape$updateShapeAttributes(state, shapeState);
                        return shapeState;
                    }
                    return null;
                });
            }
        }
    }

    // Entity-level sync — only things with no render state equivalent,
    // must run before createRenderState() so they are captured into the state
    @Unique
    private void shape$updateShapeAttributes(@NotNull AbstractClientPlayer player, @NotNull LivingEntity shape) {
        // GeckoLib delta movement — no render state equivalent
        if (player != Minecraft.getInstance().getCameraEntity()) {
            float x = (float) ((player.getX() - player.xOld) / 1.62F);
            float y = (float) ((player.getY() - player.yOld) / 1.62F);
            float z = (float) ((player.getZ() - player.zOld) / 1.62F);
            shape.setDeltaMovement(new Vec3(x, y, z));
        } else {
            shape.setDeltaMovement(player.getDeltaMovement());
        }

        // No render state equivalents
        shape.setOnGround(player.onGround());
        shape.noPhysics = true;
        ((EntityAccessor) shape).setVehicle(player.getVehicle());
        ((EntityAccessor) shape).setPassengers(ImmutableList.copyOf(player.getPassengers()));
        ((EntityAccessor) shape).setTouchingWater(player.isInWater());

        // Phantom pitch inversion — must be set before createRenderState captures xRot
        if (shape instanceof Phantom) {
            shape.setXRot(-player.getXRot());
            shape.xRotO = -player.xRotO;
        } else {
            shape.setXRot(player.getXRot());
            shape.xRotO = player.xRotO;
        }

        // Item slots — captured by createRenderState per entity type
        if (Shapes.CONFIG.shapesEquipItems) {
            shape.setItemSlot(EquipmentSlot.MAINHAND, player.getItemBySlot(EquipmentSlot.MAINHAND));
            shape.setItemSlot(EquipmentSlot.OFFHAND, player.getItemBySlot(EquipmentSlot.OFFHAND));
        }
        if (Shapes.CONFIG.shapesEquipArmor) {
            shape.setItemSlot(EquipmentSlot.HEAD, player.getItemBySlot(EquipmentSlot.HEAD));
            shape.setItemSlot(EquipmentSlot.CHEST, player.getItemBySlot(EquipmentSlot.CHEST));
            shape.setItemSlot(EquipmentSlot.LEGS, player.getItemBySlot(EquipmentSlot.LEGS));
            shape.setItemSlot(EquipmentSlot.FEET, player.getItemBySlot(EquipmentSlot.FEET));
        }

        if (shape instanceof Mob mob) {
            mob.setAggressive(player.isUsingItem());
            mob.setNoAi(true);
            if (mob instanceof EnderMan enderMan) {
                ItemStack heldStack = player.getMainHandItem();

                if (heldStack.getItem() instanceof BlockItem) {
                    enderMan.setCarriedBlock(((BlockItem) heldStack.getItem()).getBlock().defaultBlockState());
                }
            }
        }

        // Must be set before callUpdatingUsingItem captures it
        shape.setPose(player.getPose());
        player.getUsedItemHand();
        shape.startUsingItem(player.getUsedItemHand());
        ((LivingEntityAccessor) shape).callSetLivingEntityFlag(1, player.isUsingItem());
        shape.getTicksUsingItem();
        ((LivingEntityAccessor) shape).callUpdatingUsingItem();

        // Invisibility — captured by createRenderState
        if (Minecraft.getInstance().player != null) {
            shape.setInvisible(player.isInvisibleTo(Minecraft.getInstance().player));
        }

        // Only render nametags if the server option is true and the entity being
        // rendered is NOT this player/client
        if (player != Minecraft.getInstance().player && shape$showName(player)) {
            shape.setCustomName(player.getCustomName());
        }
    }

    // Render-state-level sync — runs after createRenderState, fills in what
    // createRenderState doesn't capture or gets wrong for our use case
    @Unique
    private void shape$updateShapeAttributes(@NotNull AvatarRenderState player, @NotNull EntityRenderState shape) {
        shape.y = player.y;
        shape.x = player.x;
        shape.z = player.z;
        shape.ageInTicks = player.ageInTicks;
        shape.passengerOffset = player.passengerOffset;
        shape.leashStates = player.leashStates;
        shape.lightCoords = player.lightCoords;
        shape.outlineColor = player.outlineColor;

        if (shape instanceof LivingEntityRenderState livingState) {
            livingState.pose = player.pose;
            livingState.walkAnimationPos = player.walkAnimationPos;
            livingState.walkAnimationSpeed = player.walkAnimationSpeed;
            livingState.bodyRot = player.bodyRot;
            if (livingState instanceof PhantomRenderState phantom) {
                phantom.xRot = -player.xRot;
                phantom.flapTime = player.id * 3 + player.ageInTicks; // fix phantom flapping
            } else {
                livingState.xRot = player.xRot;
            }
            if (!(livingState instanceof BatRenderState)) {
                livingState.yRot = player.yRot;
            }
            livingState.wornHeadAnimationPos = player.wornHeadAnimationPos;
            livingState.isInWater = player.isInWater;
            livingState.scale = player.scale;
            livingState.isFullyFrozen = player.isFullyFrozen;
            livingState.deathTime = player.deathTime; // Copy the death time so the shape plays the death animation

            if (livingState instanceof ArmedEntityRenderState armedState) {
                armedState.mainArm = player.mainArm;
            }

            if (shape instanceof HumanoidRenderState humanoidShape) {
                humanoidShape.swimAmount = player.swimAmount;
                humanoidShape.isVisuallySwimming = player.isVisuallySwimming;
                humanoidShape.attackArm = player.attackArm;
                humanoidShape.attackTime = player.attackTime;
                humanoidShape.speedValue = player.speedValue;
                humanoidShape.isCrouching = player.isCrouching;
                humanoidShape.ticksUsingItem = player.ticksUsingItem;
                humanoidShape.isUsingItem = player.isUsingItem;
            } else if (shape instanceof WitherRenderState witherState) {
                java.util.Arrays.fill(witherState.xHeadRots, player.xRot);
                java.util.Arrays.fill(witherState.yHeadRots, player.bodyRot + player.yRot);
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Unique
    private static boolean shape$showName(@NotNull AbstractClientPlayer player) {
        Team team = player.getTeam();
        boolean showName = Shapes.CONFIG.showPlayerNametag;

        if (showName && team != null) {
            Team.Visibility visibility = team.getNameTagVisibility();
            Team localTeam = Minecraft.getInstance().player.getTeam();
            boolean sameTeam = Objects.equals(localTeam != null ? localTeam.getName() : null, team.getName());

            if (visibility == Team.Visibility.NEVER ||
                    (sameTeam && visibility == Team.Visibility.HIDE_FOR_OWN_TEAM) ||
                    (!sameTeam && visibility == Team.Visibility.HIDE_FOR_OTHER_TEAMS)) {

                showName = false;
            }
        }
        return showName;
    }

    // Draws the shape instead of the player. Vanilla AvatarRenderer doesn't override submit, so this is called by
    // LivingEntityRendererMixin at the start of LivingEntityRenderer.submit, right before the player model is drawn.
    // Returns true when the player model must be skipped.
    @Override
    public boolean shape$submitShape(AvatarRenderState state, PoseStack matrixStack, SubmitNodeCollector buffer, CameraRenderState camera) {
        // While the player model is captured for the morph animation, draw the plain player
        if (MorphTransitions.CAPTURING.get()) {
            return false;
        }
        if (((ShapeRenderStateProvider) state).shape$getInvisRide()) {
            return true;
        }

        if (!state.isSpectator && !state.isInvisibleToPlayer && !state.isInvisible) {
            EntityRenderer<@NotNull LivingEntity, @NotNull EntityRenderState> shapeRenderer = ((ShapeRenderStateProvider) state).shape$getShapeRenderer();
            EntityRenderState shapeRenderState = shapeRenderer != null ? ((ShapeRenderStateProvider) state).shape$getShapeRenderState() : null;
            if (MorphTransitions.render((AvatarRenderer) (Object) this, state, shapeRenderer, shapeRenderState, matrixStack, buffer, camera)) {
                return true;
            }
            if (shapeRenderer != null && shapeRenderState != null) {
                shapeRenderer.submit(shapeRenderState, matrixStack, buffer, camera);
                state.shadowRadius = shapeRenderState.shadowRadius;
                return true;
            }
        }
        return false;
    }

    @Inject(method = "getRenderOffset(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
    private void modifyPositionOffset(AvatarRenderState state, CallbackInfoReturnable<Vec3> cir) {
        if (((ShapeRenderStateProvider) state).shape$getShapeRenderState() != null && ((ShapeRenderStateProvider) state).shape$shapeIsTameable()) {
            cir.setReturnValue(super.getRenderOffset(state));
        }
    }

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void onRenderArm(PoseStack matrices, SubmitNodeCollector vertexConsumers, int light, Identifier resourceLocation, ModelPart arm, boolean bl, CallbackInfo ci) {
        if (Minecraft.getInstance().getCameraEntity() instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            // sync player data to shape
            if (shape != null) {
                EntityRenderer<LivingEntity, ?> renderer = (EntityRenderer<LivingEntity, ?>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(shape);

                if (renderer instanceof LivingEntityRenderer livingRenderer) {
                    LivingEntityRenderState shapeState = ((LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>) livingRenderer).createRenderState(shape, 1.0f);
                    shapeState.lightCoords = light;
                    Identifier texture = livingRenderer.getTextureLocation(shapeState);
                    EntityModel model = livingRenderer.getModel();

                    // re-assign arm & sleeve models
                    arm = null;

                    if (model instanceof HumanoidModel) {
                        if (player.getMainArm() == HumanoidArm.RIGHT) {
                            arm = ((HumanoidModel<?>) model).rightArm;
                        } else {
                            arm = ((HumanoidModel<?>) model).leftArm;
                        }
                    } else {
                        Pair<ModelPart, ArmRenderingManipulator<EntityModel<EntityRenderState>>> pair = EntityArms.get(shape, model);
                        if (pair != null) {
                            arm = pair.getFirst();
                            // mirror matrices if the player is left-handed
                            if (player.getMainArm() == HumanoidArm.LEFT) {
                                matrices.mulPose(Maths.getDegreesQuaternion(Maths.POSITIVE_Y(), 180));
                            }
                            pair.getSecond().run(matrices, model);
                            matrices.translate(0, -.35, .5);
                        }
                    }

                    model.setupAnim(renderer.createRenderState(shape, 1.0f));

                    // render
                    if (arm != null) {
                        arm.xRot = 0.0F;
                        vertexConsumers.submitModelPart(arm, matrices, RenderTypes.entityTranslucent(texture), light, OverlayTexture.NO_OVERLAY, null);
                    }

                    ci.cancel();
                }
            }
        }
    }
}
