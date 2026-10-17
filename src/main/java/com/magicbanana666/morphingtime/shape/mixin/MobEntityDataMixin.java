package com.magicbanana666.morphingtime.shape.mixin;

import com.magicbanana666.morphingtime.shape.impl.ShapeDataProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Mob.class)
public abstract class MobEntityDataMixin extends LivingEntity implements ShapeDataProvider {
    @Unique
    private int shape$shapedPlayerId = -1;

    protected MobEntityDataMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    @Override
    public int shape$shapedPlayer() {
        return shape$shapedPlayerId;
    }

    @Unique
    @Override
    public void shape$ShapedPlayer(int id) {
        this.shape$shapedPlayerId = id;
    }
}
