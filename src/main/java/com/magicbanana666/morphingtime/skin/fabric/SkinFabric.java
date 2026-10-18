package com.magicbanana666.morphingtime.skin.fabric;

import com.magicbanana666.morphingtime.skin.PlayerSkins;

/**
 * Initializes the skin module. The permissions ("morphingtime:skin.command.set", ...) need no registration on Fabric,
 * see {@link com.magicbanana666.morphingtime.core.permission.fabric.PermissionCheckerImpl}.
 */
@SuppressWarnings("unused")
public class SkinFabric {
    public SkinFabric() {
        new PlayerSkins().initialize();
    }
}
