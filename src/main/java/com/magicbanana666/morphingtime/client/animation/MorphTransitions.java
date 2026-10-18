package com.magicbanana666.morphingtime.client.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.magicbanana666.morphingtime.MorphingTime;
import com.magicbanana666.morphingtime.client.animation.ModelCapture.CapturedCube;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

/**
 * The morph animation: when a player changes shape, the boxes of the old model slide, turn and resize into the
 * boxes of the new model, drawn as glowing white blocks.
 */
public final class MorphTransitions {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MorphingTime.MODID, "textures/misc/morph.png");
    /** Set while the player model is being captured, so the renderer draws the plain player into the capture. */
    public static final ThreadLocal<Boolean> CAPTURING = ThreadLocal.withInitial(() -> false);

    /** Marks "the player itself" as what was drawn. */
    private static final Object PLAYER = new Object();
    private static final Map<Integer, Track> TRACKS = new HashMap<>();
    /** How long a player may be out of view while still showing the animation of their morph. */
    private static final float RECENT_TICKS = 20;
    private static @Nullable Level lastLevel;

    private MorphTransitions() {
    }

    private static final class Track {
        @Nullable Object key;
        @Nullable EntityRenderer<?, EntityRenderState> renderer;
        @Nullable EntityRenderState state;
        @Nullable List<Pair> pairs;
        float start;
        float lastSeen;
    }

    private record Pair(CapturedCube from, CapturedCube to) {
    }

    /**
     * Called for every player that is drawn. Starts a transition when the drawn shape changed and draws it while it
     * runs. Returns true when the transition was drawn, so the normal model must be skipped.
     */
    @SuppressWarnings("unchecked")
    public static boolean render(AvatarRenderer renderer, AvatarRenderState state, @Nullable EntityRenderer<?, ? extends EntityRenderState> shapeRenderer,
                                 @Nullable EntityRenderState shapeState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!Shapes.CONFIG.morphAnimation || Minecraft.getInstance().level == null) {
            return false;
        }
        // Entity ids and game time belong to one world, so forget everything when it changes
        if (Minecraft.getInstance().level != lastLevel) {
            TRACKS.clear();
            lastLevel = Minecraft.getInstance().level;
        }
        float now = Minecraft.getInstance().level.getGameTime() + Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        boolean isShape = shapeRenderer != null && shapeState != null;
        // Keyed by the entity type and not the renderer, renderers are created again when resources reload
        Object key = isShape ? shapeState.entityType : PLAYER;
        Track track = TRACKS.computeIfAbsent(state.id, id -> new Track());

        // Only animate when the player was drawn just before, otherwise the shape changed while they were out of
        // view and the animation would play long after the morph
        if (track.key != null && !track.key.equals(key) && now - track.lastSeen <= RECENT_TICKS) {
            List<CapturedCube> from = track.key == PLAYER ? capturePlayer(renderer, state, camera) : captureShape(track.renderer, track.state, camera);
            List<CapturedCube> to = isShape ? captureShape((EntityRenderer<?, EntityRenderState>) shapeRenderer, shapeState, camera) : capturePlayer(renderer, state, camera);
            track.pairs = match(from, to);
            track.start = now;
        }
        track.key = key;
        track.renderer = isShape ? (EntityRenderer<?, EntityRenderState>) shapeRenderer : null;
        track.state = isShape ? shapeState : null;
        track.lastSeen = now;
        cleanUp(now);

        if (track.pairs == null) {
            return false;
        }
        float progress = (now - track.start) / Math.max(1, Shapes.CONFIG.morphAnimationTicks);
        if (progress >= 1F || progress < 0F) {
            track.pairs = null;
            return false;
        }
        draw(track.pairs, ease(progress), poseStack, collector);
        return true;
    }

    private static void draw(List<Pair> pairs, float t, PoseStack poseStack, SubmitNodeCollector collector) {
        // Glows brightest in the middle of the change
        int alpha = (int) (200 + 55 * Mth.sin(t * Mth.PI));
        int color = ARGB.color(alpha, 255, 255, 255);
        RenderType type = RenderTypes.entityTranslucentEmissive(TEXTURE);
        List<CapturedCube> cubes = new ArrayList<>(pairs.size());
        for (Pair pair : pairs) {
            cubes.add(pair.from().lerp(pair.to(), t));
        }
        collector.submitCustomGeometry(poseStack, type, (pose, consumer) -> {
            for (CapturedCube cube : cubes) {
                cube.draw(pose, consumer, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color, 0, 0, 16, 16, 0F);
            }
        });
    }

    private static List<CapturedCube> capturePlayer(AvatarRenderer renderer, AvatarRenderState state, CameraRenderState camera) {
        ModelCapture capture = new ModelCapture();
        CAPTURING.set(true);
        try {
            renderer.submit(state, new PoseStack(), capture, camera);
        } catch (RuntimeException e) {
            Shapes.LOGGER.debug("Could not capture the player model for the morph animation", e);
        } finally {
            CAPTURING.set(false);
        }
        return new ArrayList<>(capture.getCubes());
    }

    static List<CapturedCube> captureShape(@Nullable EntityRenderer<?, EntityRenderState> renderer, @Nullable EntityRenderState state, CameraRenderState camera) {
        if (renderer == null || state == null) {
            return List.of();
        }
        ModelCapture capture = new ModelCapture();
        try {
            renderer.submit(state, new PoseStack(), capture, camera);
        } catch (RuntimeException e) {
            Shapes.LOGGER.debug("Could not capture a model for the morph animation", e);
        }
        return new ArrayList<>(capture.getCubes());
    }

    /** Pairs every box of the old model with the nearest box of the new one; leftovers grow from or shrink to a point. */
    private static List<Pair> match(List<CapturedCube> from, List<CapturedCube> to) {
        List<Pair> pairs = new ArrayList<>();
        List<CapturedCube> remaining = new ArrayList<>(to);
        for (CapturedCube a : from) {
            Vector3f ca = center(a);
            CapturedCube best = null;
            float bestDistance = Float.MAX_VALUE;
            for (CapturedCube b : remaining) {
                float distance = ca.distanceSquared(center(b));
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = b;
                }
            }
            if (best != null) {
                remaining.remove(best);
                pairs.add(new Pair(a, best));
            } else {
                pairs.add(new Pair(a, a.collapsed()));
            }
        }
        for (CapturedCube b : remaining) {
            pairs.add(new Pair(b.collapsed(), b));
        }
        return pairs;
    }

    private static Vector3f center(CapturedCube cube) {
        return cube.pose().transformPosition(cube.centerX() / 16F, cube.centerY() / 16F, cube.centerZ() / 16F, new Vector3f());
    }

    static float ease(float t) {
        return t * t * (3F - 2F * t);
    }

    /** Forgets players that haven't been drawn for a while. */
    private static void cleanUp(float now) {
        if (TRACKS.size() < 64) {
            return;
        }
        Iterator<Track> it = TRACKS.values().iterator();
        while (it.hasNext()) {
            if (now - it.next().lastSeen > 200) {
                it.remove();
            }
        }
    }
}
