package com.magicbanana666.morphingtime.shape.impl.variant;

import com.magicbanana666.morphingtime.shape.api.variant.TypeProvider;
import com.magicbanana666.morphingtime.shape.mixin.accessor.FoxEntityAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FoxTypeProvider extends TypeProvider<Fox> {

    @Override
    public int getVariantData(Fox entity) {
        return entity.getVariant().getId();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Fox create(EntityType<Fox> type, Level world, @NotNull Player player, int data) {
        Fox fox = new Fox(type, world);
        ((FoxEntityAccessor) fox).callSetVariant(Fox.Variant.byId(data));
        return fox;
    }

    @Override
    public int getFallbackData() {
        return Fox.Variant.RED.getId();
    }

    @Override
    public int size(Level level) {
        return Fox.Variant.values().length;
    }

    @Override
    public Component modifyText(Fox entity, MutableComponent text) {
        return Component.literal(formatTypePrefix(Fox.Variant.byId(getVariantData(entity)).name()) + " ").append(text);
    }
}
