package com.magicbanana666.morphingtime.core.event.common;

import com.magicbanana666.morphingtime.core.event.Event;
import com.magicbanana666.morphingtime.core.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings("unused")
public final class ResourceEvents {
    public static final Event<DataPackSync> DATA_PACK_SYNC = EventFactory.createWithVoid();

    @FunctionalInterface
    public interface DataPackSync {
        void onSync(ServerPlayer player);
    }
}
