package com.magicbanana666.morphingtime.shape.api.model.impl;

import com.magicbanana666.morphingtime.shape.api.model.EntityUpdater;
import com.magicbanana666.morphingtime.shape.mixin.accessor.ShulkerAccessor;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;


public class ShulkerEntityUpdater implements EntityUpdater<Shulker> {
    @Override
    public void update(Player player, Shulker shulker) {
        if (((ShulkerAccessor) shulker).callGetRawPeekAmount() <= 0 && shulker.getClientPeekAmount(0) <= 0 && shulker.getRandom().nextInt(50) == 1) {
            ((ShulkerAccessor) shulker).callSetRawPeekAmount(30);
        } else if (shulker.getClientPeekAmount(0) >= 1) {
            ((ShulkerAccessor) shulker).callSetRawPeekAmount(0);
        }

        if (((ShulkerAccessor) shulker).callUpdatePeekAmount()) ((ShulkerAccessor) shulker).callOnPeekAmountChange();
    }
}
