package com.magicbanana666.morphingtime.core.registration.fabric;

import com.magicbanana666.morphingtime.core.registration.RegistryRegistryService;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

public class RegistryRegistryImpl implements RegistryRegistryService {
    @Override
    public <T> @NotNull Registry<T> createSimpleRegistry(ResourceKey<Registry<T>> key) {
        return FabricRegistryBuilder.create(key).buildAndRegister();
    }
}
