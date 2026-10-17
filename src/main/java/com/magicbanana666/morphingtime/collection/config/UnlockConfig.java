package com.magicbanana666.morphingtime.collection.config;

import com.magicbanana666.morphingtime.core.config.Config;
import com.magicbanana666.morphingtime.core.config.annotions.Comment;
import com.magicbanana666.morphingtime.core.config.annotions.Synchronize;
import com.magicbanana666.morphingtime.collection.MorphCollection;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("CanBeFinal")
public class UnlockConfig implements Config {
    @Comment("<<< General >>>\nEnable the permission system. When true, permission nodes decide what players can do. When false, the settings in this config are used.")
    @Synchronize
    public boolean usePermissions = false;
    @Synchronize
    public boolean creativeUnlockAll = true;
    @Synchronize
    public int killToUnlock = 1;
    @Synchronize
    public int killToUnlockPlayers = 2;
    @Synchronize
    @Comment("After morphing into a shape this many times, one unlock's worth of kills is used up and the shape may lock again. 0 (default) keeps unlocked shapes forever.")
    public int killValue = 0;
    @Synchronize
    public int playerKillValue = 0;
    public boolean autoTransform = false;
    @Synchronize
    public boolean lockTransform = false;
    @Comment("Whether friendly mobs can be unlocked with the unlock key while looking at them, instead of by killing them.")
    public boolean unlockFriendlyNormal = false;
    @Synchronize
    public Map<String, Integer> killToUnlockByType = new HashMap<>() {
        {
            put("minecraft:ender_dragon", 1);
            put("minecraft:wither", 1);
        }
    };
    @Synchronize
    public Map<String, Integer> killValueByType = new HashMap<>();
    @Comment("Whether the player can open the Variants menu or not.")
    public boolean allow_variant_selection = true;
    @Comment("\n<<< Menu Customization >>>")
    public boolean show_traits_by_default = true;
    @Comment("Scale of the shapes in the menu.")
    public int entity_size = 20;
    public int shapes_per_row = 6;
    @Comment("Whether the selected shape is shown first in the menu.")
    public boolean sort_selected = false;
    @Comment("Whether the menu automatically scrolls to the selected shape.")
    public boolean focus_selected = true;
    @Comment("The width of the rows in the menu.")
    public int row_width = 330;

    @Override
    public String getName() {
        return MorphCollection.MODID;
    }
}
