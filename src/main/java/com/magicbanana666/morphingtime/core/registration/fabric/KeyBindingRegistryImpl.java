package com.magicbanana666.morphingtime.core.registration.fabric;

import com.magicbanana666.morphingtime.core.registration.KeyBindingRegistryService;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

@SuppressWarnings("unused")

public final class KeyBindingRegistryImpl implements KeyBindingRegistryService {
    @Override
    public void register(KeyMapping mapping) {
        KeyMappingHelper.registerKeyMapping(mapping);
    }
}
