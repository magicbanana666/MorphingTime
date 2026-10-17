package com.magicbanana666.morphingtime.core.neoforge;

import com.magicbanana666.morphingtime.core.MorphCore;
import com.magicbanana666.morphingtime.core.neoforge.client.CoreNeoForgeClient;
import com.magicbanana666.morphingtime.core.registration.neoforge.RegistryRegistryImpl;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
@ApiStatus.Internal
// Created by com.magicbanana666.morphingtime.MorphingTime during mod construction
public class CoreNeoForge {

    public CoreNeoForge(IEventBus bus) {
        if (FMLEnvironment.getDist().isClient()) {
            new CoreNeoForgeClient(bus);
        }
        new MorphCore().initialize();

        NeoForge.EVENT_BUS.register(new CoreNeoForgeEventHandler());

        bus.addListener(CoreNeoForge::registerRegistries);

    }

    private static void registerRegistries(@NotNull NewRegistryEvent event) {
        for (Registry<?> registry : RegistryRegistryImpl.getRegistries()) {
            event.register(registry);
        }
    }

    public static IEventBus getEventBus() {
        return Objects.requireNonNull(ModList.get().getModContainerById(com.magicbanana666.morphingtime.MorphingTime.MODID).orElseThrow().getEventBus());
    }
}
