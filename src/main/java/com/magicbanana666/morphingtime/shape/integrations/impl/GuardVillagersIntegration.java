package com.magicbanana666.morphingtime.shape.integrations.impl;

import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.integrations.AbstractIntegration;
import com.magicbanana666.morphingtime.shape.mixin.accessor.EntityAccessor;
import com.magicbanana666.morphingtime.shape.traits.TraitRegistry;
import com.magicbanana666.morphingtime.shape.traits.impl.PreyTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class GuardVillagersIntegration extends AbstractIntegration {
    public static final String MODID = "guardvillagers";
    public static final Identifier GUARD_VILLAGER_TYPE = Identifier.fromNamespaceAndPath(MODID, "guard");

    @Override
    public void registerTraits() {
        List<String> mobBlacklist = getMobBlackList();
        TraitRegistry.registerByPredicate(entity -> entity instanceof Enemy && mobBlacklist != null && !mobBlacklist.contains(((EntityAccessor) entity).callGetEncodeId()), new PreyTrait<>(List.of(hunter -> EntityType.getKey(hunter.getType()).equals(GUARD_VILLAGER_TYPE))));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private List<String> getMobBlackList() {
        try {
            Class<?> configClass = Class.forName("tallestegg.guardvillagers.configuration.GuardConfig");
            Object commonConfig = configClass.getDeclaredField("COMMON").get(null);
            Field MobBlackListField = commonConfig.getClass().getDeclaredField("MobBlackList");
            Object MobBlacklist = MobBlackListField.get(commonConfig);
            Method getMobBlacklist = MobBlacklist.getClass().getDeclaredMethod("get");
            return (List<String>) getMobBlacklist.invoke(MobBlacklist);
        } catch (ReflectiveOperationException e) {
            Shapes.LOGGER.error("{}: failed to get the mob blacklist: {}", GuardVillagersIntegration.class, e);
            return null;
        }
    }
}
