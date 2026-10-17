package com.magicbanana666.morphingtime.skin;

import com.magicbanana666.morphingtime.core.config.Config;
import com.magicbanana666.morphingtime.core.config.annotions.Comment;
import com.magicbanana666.morphingtime.core.config.annotions.Synchronize;

@SuppressWarnings("CanBeFinal")
public class SkinConfig implements Config {
    @Comment("Enable the permission system. When true, permission nodes decide who can use the skin commands. When false, the op levels below are used.")
    public boolean usePermissions = false;
    @Comment("Op level needed to use skin commands on other players and to toggle chat names. Only used when 'usePermissions' is false.")
    public int baseCommandOPLevel = 2;
    @Comment("Op level needed to use skin commands on yourself. Only used when 'usePermissions' is false.")
    public int selfCommandOPLevel = 0;
    @Synchronize
    public boolean changeName = true;

    @Override
    public String getName() {
        return PlayerSkins.MODID;
    }
}
