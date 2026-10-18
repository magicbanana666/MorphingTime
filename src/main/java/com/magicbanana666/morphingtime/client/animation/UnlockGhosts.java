package com.magicbanana666.morphingtime.client.animation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.magicbanana666.morphingtime.client.animation.ModelCapture.CapturedCube;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;

/**
 * The unlock animation: a glowing copy of the mob you just unlocked flies into you and shrinks away.
 */
public final class UnlockGhosts {

    private static final float DURATION = 30F;
    private static final List<Ghost> GHOSTS = new ArrayList<>();

    private UnlockGhosts() {
    }

    private record Ghost(List<CapturedCube> cubes, Vec3 from, float height, float start) {
    }

    /** Starts the animation for the given (dying) entity. */
    @SuppressWarnings("unchecked")
    public static void start(int entityId) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!Shapes.CONFIG.unlockAnimation || minecraft.level == null) {
            return;
        }
        Entity entity = minecraft.level.getEntity(entityId);
        if (!(entity instanceof LivingEntity living)) {
            return;
        }
        EntityRenderer<LivingEntity, EntityRenderState> renderer = (EntityRenderer<LivingEntity, EntityRenderState>) minecraft.getEntityRenderDispatcher().getRenderer(living);
        EntityRenderState state = renderer.createRenderState(living, 1F);
        if (state instanceof LivingEntityRenderState livingState) {
            // Show the mob standing up, not falling over
            livingState.deathTime = 0F;
            livingState.hasRedOverlay = false;
        }
        CameraRenderState camera = new CameraRenderState();
        camera.pos = minecraft.gameRenderer.mainCamera().position();
        List<CapturedCube> cubes = MorphTransitions.captureShape(renderer, state, camera);
        if (!cubes.isEmpty()) {
            GHOSTS.add(new Ghost(cubes, living.position(), living.getBbHeight(), minecraft.level.getGameTime()));
        }
    }

    public static void onCollectSubmits(LevelRenderContext context) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (GHOSTS.isEmpty() || player == null || minecraft.level == null) {
            return;
        }
        float partial = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float now = minecraft.level.getGameTime() + partial;
        Vec3 camera = context.levelState().cameraRenderState.pos;
        Vec3 target = player.getPosition(partial).add(0, player.getBbHeight() * 0.4, 0);
        PoseStack poseStack = context.poseStack();

        Iterator<Ghost> it = GHOSTS.iterator();
        while (it.hasNext()) {
            Ghost ghost = it.next();
            float progress = (now - ghost.start()) / DURATION;
            if (progress >= 1F) {
                it.remove();
                minecraft.level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6F, 0.6F, false);
                continue;
            }
            if (progress < 0F) {
                continue;
            }
            float t = MorphTransitions.ease(progress);
            // Fly in an arc from where the mob died into the player, shrinking on the way
            Vec3 pos = ghost.from().lerp(target, t).add(0, Mth.sin(t * Mth.PI) * 0.8, 0);
            float scale = 1F - 0.85F * t;
            int alpha = (int) (220 * (1F - progress * progress));
            int color = ARGB.color(alpha, 220, 240, 255);

            poseStack.pushPose();
            poseStack.translate(pos.x - camera.x, pos.y - camera.y, pos.z - camera.z);
            // Scale around the middle of the mob so it doesn't sink into the ground
            poseStack.translate(0, ghost.height() / 2F, 0);
            poseStack.scale(scale, scale, scale);
            poseStack.translate(0, -ghost.height() / 2F, 0);
            List<CapturedCube> cubes = ghost.cubes();
            context.submitNodeCollector().submitCustomGeometry(poseStack, RenderTypes.entityTranslucentEmissive(MorphTransitions.TEXTURE), (pose, consumer) -> {
                for (CapturedCube cube : cubes) {
                    cube.draw(pose, consumer, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color, 0, 0, 16, 16, 0F);
                }
            });
            poseStack.popPose();
        }
    }
}
