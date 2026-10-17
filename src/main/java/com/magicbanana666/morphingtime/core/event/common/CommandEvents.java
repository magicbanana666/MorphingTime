package com.magicbanana666.morphingtime.core.event.common;

import com.mojang.brigadier.CommandDispatcher;
import com.magicbanana666.morphingtime.core.event.Event;
import com.magicbanana666.morphingtime.core.event.EventFactory;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

@SuppressWarnings("unused")
public final class CommandEvents {
    public static final Event<CommandRegistration> REGISTRATION = EventFactory.createWithVoid();

    @FunctionalInterface
    public interface CommandRegistration {
        void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection selection);
    }
}
