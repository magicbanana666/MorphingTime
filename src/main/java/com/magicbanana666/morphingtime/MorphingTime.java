package com.magicbanana666.morphingtime;

import com.magicbanana666.morphingtime.core.neoforge.CoreNeoForge;
import com.magicbanana666.morphingtime.collection.neoforge.CollectionNeoForge;
import com.magicbanana666.morphingtime.shape.neoforge.ShapesNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * Mod entry point: kill mobs to unlock them, then morph into them.
 * <p>
 * Registers the data attachments and initializes the core, morphing, skin and unlocking modules.
 * The order matters, because each module depends on the ones initialized before it.
 */
@Mod(MorphingTime.MODID)
public final class MorphingTime {

    public static final String MODID = "morphingtime";

    public MorphingTime(IEventBus modBus, ModContainer container) {
        MorphingTimeAttachments.REGISTER.register(modBus);
        modBus.addListener(RegisterPayloadHandlersEvent.class, event ->
                event.registrar("1").playToClient(UnlockEffectPayload.TYPE, UnlockEffectPayload.STREAM_CODEC));
        if (FMLEnvironment.getDist().isClient()) {
            com.magicbanana666.morphingtime.client.MorphingTimeClient.init(modBus);
        }
        new CoreNeoForge(modBus);
        new ShapesNeoForge();
        new com.magicbanana666.morphingtime.skin.neoforge.SkinNeoForge();
        new CollectionNeoForge();
    }
}
