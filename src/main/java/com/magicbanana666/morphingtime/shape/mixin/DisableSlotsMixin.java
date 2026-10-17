package com.magicbanana666.morphingtime.shape.mixin;

import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(value = LivingEntity.class)
public abstract class DisableSlotsMixin {
    @Inject(method = "getItemBySlot", at = @At("HEAD"), cancellable = true)
    private void onGetAmorItem(@NotNull EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        if (slot.isArmor() && shape$blockEquipSlot(slot)) {
            cir.setReturnValue(ItemStack.EMPTY); // Pretend there is no item in that slot
        }
    }

    @Inject(method = "canUseSlot", at = @At("HEAD"), cancellable = true)
    private void onCanUseSlot(@NotNull EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir) {
        if (slot.isArmor() && shape$blockEquipSlot(slot)) {
            cir.setReturnValue(false); // prevent equipping
        }
    }

    @Unique
    private boolean shape$blockEquipSlot(EquipmentSlot slot) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape == null) return false;

            String shapeKey = EntityType.getKey(shape.getType()).toString();

            Set<String> blockedSlots = new HashSet<>(shape$getBlockedSlots("*"));
            blockedSlots.addAll(shape$getBlockedSlots(shapeKey));

            return blockedSlots.contains(slot.getName());
        }
        return false;
    }

    @Unique
    private List<String> shape$getBlockedSlots(String key) {
        return Shapes.CONFIG.blockEquipmentSlots.getOrDefault(key, Collections.emptyList());
    }

}