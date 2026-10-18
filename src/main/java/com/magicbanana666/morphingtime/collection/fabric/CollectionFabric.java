package com.magicbanana666.morphingtime.collection.fabric;

import com.magicbanana666.morphingtime.collection.MorphCollection;

/**
 * Initializes the unlocking module. The permissions ("morphingtime:morph", "morphingtime:command.addShape",
 * "morphingtime:type.minecraft.pig", ...) need no registration on Fabric, see
 * {@link com.magicbanana666.morphingtime.core.permission.fabric.PermissionCheckerImpl}.
 */
@SuppressWarnings("unused")
public class CollectionFabric {
    public CollectionFabric() {
        new MorphCollection().initialize();
    }
}
