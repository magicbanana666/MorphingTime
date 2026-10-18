package com.magicbanana666.morphingtime.core.registration.fabric;

import com.magicbanana666.morphingtime.core.data.SynchronizedJsonReloadListener;
import com.magicbanana666.morphingtime.core.registration.SynchronizedReloadListenerRegistryService;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("unused")
@ApiStatus.Internal
public class SynchronizedReloadListenerRegistryImpl implements SynchronizedReloadListenerRegistryService {
    @Override
    public void onRegister(SynchronizedJsonReloadListener reloadListener, Identifier id) {
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(id, reloadListener);
    }
}
