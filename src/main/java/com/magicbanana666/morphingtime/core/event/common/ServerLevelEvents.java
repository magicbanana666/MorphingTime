package com.magicbanana666.morphingtime.core.event.common;

import com.magicbanana666.morphingtime.core.event.Event;
import com.magicbanana666.morphingtime.core.event.EventFactory;
import net.minecraft.server.level.ServerLevel;

@SuppressWarnings("unused")
public final class ServerLevelEvents {
    public static final Event<LevelEvent> LEVEL_LOAD = EventFactory.createWithVoid();
    public static final Event<LevelEvent> LEVEL_UNLOAD = EventFactory.createWithVoid();

    @FunctionalInterface
    public interface LevelEvent {
        void call(ServerLevel level);
    }
}
