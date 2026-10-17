package com.magicbanana666.morphingtime.shape.impl;

import java.util.Map;

import com.magicbanana666.morphingtime.MorphingTime;
import com.magicbanana666.morphingtime.shape.Shapes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Gives a player the stats (attributes) of the mob they morph into, such as attack damage, speed and
 * knockback resistance. Every attribute works, so modded mobs and attributes added by other mods are
 * supported too. Health, armor, scale and step height are handled separately in the player data mixin.
 */
public final class ShapeStats {

    private static final Identifier MODIFIER_ID = Identifier.fromNamespaceAndPath(MorphingTime.MODID, "shape_stats");

    private ShapeStats() {
    }

    /** Applies the stats of the shape, or removes them when the shape is null. */
    public static void update(Player player, @Nullable LivingEntity shape) {
        for (Map.Entry<String, Double> entry : Shapes.CONFIG.copiedStats.entrySet()) {
            Holder<Attribute> attribute = lookup(entry.getKey());
            if (attribute == null) {
                continue;
            }
            AttributeInstance playerStat = player.getAttribute(attribute);
            if (playerStat == null) {
                continue;
            }
            playerStat.removeModifier(MODIFIER_ID);
            if (shape == null || !Shapes.CONFIG.copyMobStats) {
                continue;
            }
            AttributeInstance shapeStat = shape.getAttribute(attribute);
            if (shapeStat == null) {
                // The mob doesn't have this stat (EG a cow has no attack damage), keep the player's
                continue;
            }
            double target = shapeStat.getValue();
            if (attribute.is(Attributes.MOVEMENT_SPEED.unwrapKey().orElseThrow())) {
                // Mobs use other units for their walking speed than players
                target *= Shapes.CONFIG.movementSpeedFactor;
            }
            Double limit = entry.getValue();
            if (limit != null && limit >= 0) {
                target = Math.min(target, limit);
            }
            double difference = target - playerStat.getBaseValue();
            if (Math.abs(difference) > 1.0E-6) {
                playerStat.addOrUpdateTransientModifier(new AttributeModifier(MODIFIER_ID, difference, AttributeModifier.Operation.ADD_VALUE));
            }
        }
    }

    private static @Nullable Holder<Attribute> lookup(String id) {
        Identifier identifier = Identifier.tryParse(id);
        if (identifier == null) {
            Shapes.LOGGER.warn("Invalid stat id in the config: {}", id);
            return null;
        }
        return BuiltInRegistries.ATTRIBUTE.get(identifier).orElse(null);
    }
}
