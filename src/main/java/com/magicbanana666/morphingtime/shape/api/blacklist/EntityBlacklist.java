package com.magicbanana666.morphingtime.shape.api.blacklist;

import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.integrations.AbstractIntegration;
import com.magicbanana666.morphingtime.shape.integrations.Integrations;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public class EntityBlacklist {
    private static final List<EntityType<?>> typeBlacklist = new ArrayList<>();
    private static final List<TagKey<EntityType<?>>> tagBlacklist = new ArrayList<>();

    @ApiStatus.Internal
    public static void registerDefault() {
        // support deprecated entity tags
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Shapes.id("blacklisted")));

        // handle Integrations
        Integrations.registerEntityBlacklist();
    }

    public static boolean isBlacklisted(EntityType<?> entityType) {
        if (typeBlacklist.contains(entityType)) return true;
        for (TagKey<EntityType<?>> entityTypeTagKey : tagBlacklist) {
            if (entityType.builtInRegistryHolder().is(entityTypeTagKey)) return true;
        }

        return Shapes.CONFIG.entityBlacklistIsWhitelist != Shapes.CONFIG.entityBlacklist.contains(EntityType.getKey(entityType).toString());
    }

    /**
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerEntityBlacklist() Integration.registerEntityBlacklist()}
     */
    public static void registerByType(EntityType<?> entityType) {
        if (!typeBlacklist.contains(entityType)) typeBlacklist.add(entityType);
    }

    /**
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerEntityBlacklist() Integration.registerEntityBlacklist()}
     */
    public static void registerByTag(TagKey<EntityType<?>> entityTypeTag) {
        if (!tagBlacklist.contains(entityTypeTag)) tagBlacklist.add(entityTypeTag);
    }

    @ApiStatus.Internal
    public static void clearAll() {
        typeBlacklist.clear();
        tagBlacklist.clear();
    }
}
