package com.magicbanana666.morphingtime.shape.api.data;

import com.magicbanana666.morphingtime.core.registration.SynchronizedReloadListenerRegistry;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.api.data.abilities.AbilityDataManager;
import com.magicbanana666.morphingtime.shape.api.data.blacklist.EntityBlacklistDataManager;
import com.magicbanana666.morphingtime.shape.api.data.traits.TraitDataManager;
import com.magicbanana666.morphingtime.shape.api.data.variants.TypeProviderDataManager;

public class DataManager {
    public static void initialize() {
        SynchronizedReloadListenerRegistry.register(new AbilityDataManager(), Shapes.id("ability_data_manager"));
        SynchronizedReloadListenerRegistry.register(new TypeProviderDataManager(), Shapes.id("variants_data_manager"));
        SynchronizedReloadListenerRegistry.register(new TraitDataManager(), Shapes.id("trait_data_manager"));
        SynchronizedReloadListenerRegistry.register(new EntityBlacklistDataManager(), Shapes.id("entity_blacklist_data_manager"));
    }
}
