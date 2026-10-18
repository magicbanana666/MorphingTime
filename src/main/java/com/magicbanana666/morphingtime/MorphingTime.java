package com.magicbanana666.morphingtime;

import com.magicbanana666.morphingtime.core.fabric.CoreFabric;
import com.magicbanana666.morphingtime.collection.fabric.CollectionFabric;
import com.magicbanana666.morphingtime.shape.fabric.ShapesFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * Mod entry point: kill mobs to unlock them, then morph into them.
 * <p>
 * Registers the data attachments and initializes the core, morphing, skin and unlocking modules.
 * The order matters, because each module depends on the ones initialized before it.
 * On the client the client side of each module is initialized together with it, the remaining
 * client hooks are set up by {@link com.magicbanana666.morphingtime.client.MorphingTimeClient}.
 */
public final class MorphingTime implements ModInitializer {

    public static final String MODID = "morphingtime";

    @Override
    public void onInitialize() {
        MorphingTimeAttachments.init();
        PayloadTypeRegistry.clientboundPlay().register(UnlockEffectPayload.TYPE, UnlockEffectPayload.STREAM_CODEC);
        new CoreFabric();
        new ShapesFabric();
        new com.magicbanana666.morphingtime.skin.fabric.SkinFabric();
        new CollectionFabric();
    }
}
