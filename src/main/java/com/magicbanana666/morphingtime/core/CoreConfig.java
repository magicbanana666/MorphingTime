package com.magicbanana666.morphingtime.core;

import com.magicbanana666.morphingtime.core.config.Config;
import com.magicbanana666.morphingtime.core.config.ConfigLoader;
import com.magicbanana666.morphingtime.core.config.annotions.Comment;

@SuppressWarnings("CanBeFinal")
public final class CoreConfig implements Config {
    public static final CoreConfig INSTANCE = ConfigLoader.register(MorphCore.MODID);
    @Comment("Toggle the check for newer versions of the mod")
    public boolean enableVersionChecking = true;
    @Comment("Update cached player profiles in the background")
    public boolean autoUpdateCache = true;

    @Override
    public String getName() {
        return MorphCore.MODID;
    }
}
