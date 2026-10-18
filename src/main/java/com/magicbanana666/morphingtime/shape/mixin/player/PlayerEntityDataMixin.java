package com.magicbanana666.morphingtime.shape.mixin.player;

import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.api.FlightHelper;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import com.magicbanana666.morphingtime.shape.impl.ShapeStats;
import com.magicbanana666.morphingtime.shape.impl.PlayerDataProvider;
import com.magicbanana666.morphingtime.shape.mixin.EntityTrackerAccessor;
import com.magicbanana666.morphingtime.shape.mixin.ThreadedAnvilChunkStorageAccessor;
import com.magicbanana666.morphingtime.shape.traits.TraitRegistry;
import com.magicbanana666.morphingtime.shape.traits.impl.RiderTrait;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;

import static com.magicbanana666.morphingtime.shape.Shapes.WAYPOINT_TRANSMIT_MODIFIER;

@SuppressWarnings("UnreachableCode")
@Mixin(Player.class)
public abstract class PlayerEntityDataMixin extends LivingEntity implements PlayerDataProvider {
    @Unique
    private static final String ABILITY_COOLDOWN_KEY = "AbilityCooldown";
    @Unique
    @Nullable
    private ShapeType<?> shape$unlocked;
    @Unique
    private int shape$remainingTime = 0;
    @Unique
    private int shape$abilityCooldown = 0;
    @Unique
    @Nullable
    private LivingEntity shape$shape = null;
    @Unique
    private boolean shape$specialAnimation = false;

    @Unique
    private float shape$normalHealth = -1;

    private PlayerEntityDataMixin(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void readNbt(ValueInput in, CallbackInfo ci) {
        // Unlocked shape, including variant metadata
        ValueInput unlockedShape = in.childOrEmpty("UnlockedShape");
        this.shape$unlocked = ShapeType.from(unlockedShape);

        // Abilities
        shape$abilityCooldown = in.getInt(ABILITY_COOLDOWN_KEY).orElse(0);

        // Hostility
        shape$remainingTime = in.getInt("RemainingHostilityTime").orElse(0);

        // Health
        shape$normalHealth = in.getFloatOr("walkersHealth", -1);

        // Current shape
        shape$readCurrentShape(in.childOrEmpty("CurrentShape"));

        // The mob stats are temporary modifiers that aren't saved, so apply them again
        ShapeStats.update((Player) (Object) this, shape$shape);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void writeNbt(ValueOutput out, CallbackInfo ci) {
        // Unlocked shape
        CompoundTag id = new CompoundTag();
        if (shape$unlocked != null)
            id = shape$unlocked.writeCompound();
        out.store("UnlockedShape", CompoundTag.CODEC, id);

        // Abilities
        out.putInt(ABILITY_COOLDOWN_KEY, shape$abilityCooldown);

        // Hostility
        out.putInt("RemainingHostilityTime", shape$remainingTime);

        // Health
        out.putFloat("walkersHealth", shape$normalHealth);

        // Current shape
        shape$writeCurrentShape(out.child("CurrentShape"));
    }

    @Unique
    private void shape$writeCurrentShape(@NotNull ValueOutput out) {
        ValueOutput shapeOut = out.child("EntityData");

        // serialize current shapeAttackDamage data to tag if it exists
        if (shape$shape != null) {
            shape$shape.saveWithoutId(shapeOut);
        }

        // put entity type ID under the key "id", or "minecraft:empty" if no shape is
        // equipped (or the shape entity type is invalid)
        out.putString("id",
                shape$shape == null ? "minecraft:empty" : EntityType.getKey(shape$shape.getType()).toString());
    }

    @Unique
    public void shape$readCurrentShape(ValueInput in) {
        // set shape to null (no shape) if the entity id is "minecraft:empty"
        if (in.getString("id").map(it -> it.equals("minecraft:empty")).orElse(false)) {
            this.shape$shape = null;
            this.refreshDimensions();
            return;
        }

        // if entity type was valid, deserialize entity data from tag
        Optional<EntityType<?>> type = EntityType.by(in);
        if (type.isPresent()) {
            Optional<ValueInput> entityTag = in.child("EntityData");

            // ensure entity data exists
            if (entityTag.isPresent()) {
                if (shape$shape == null || !type.get().equals(shape$shape.getType())) {
                    shape$shape = (LivingEntity) type.get().create(this.level(), EntitySpawnReason.LOAD);

                    // refresh player dimensions/hitbox on client
                    this.refreshDimensions();
                }

                if (shape$shape != null) {
                    shape$shape.load(entityTag.get());
                }
            }
        }
    }

    @Unique
    @Override
    @Nullable
    public ShapeType<?> shape$get2ndShape() {
        return shape$unlocked;
    }

    @Override
    public void shape$set2ndShape(ShapeType<?> unlocked) {
        this.shape$unlocked = unlocked;
    }

    @Unique
    @Override
    public int shape$getRemainingHostilityTime() {
        return shape$remainingTime;
    }

    @Unique
    @Override
    public void shape$setRemainingHostilityTime(int max) {
        shape$remainingTime = max;
    }

    @Unique
    @Override
    public int shape$getAbilityCooldown() {
        return shape$abilityCooldown;
    }

    @Unique
    @Override
    public void shape$setAbilityCooldown(int abilityCooldown) {
        this.shape$abilityCooldown = abilityCooldown;
    }

    @Unique
    @Override
    @Nullable
    public LivingEntity shape$getCurrentShape() {
        return shape$shape;
    }

    @Unique
    @Override
    public void shape$setCurrentShape(LivingEntity shape) {
        this.shape$shape = shape;
        if (shape != null) {
            // Tag the shape with its player so other mods (EG Hats Reinvented) can find who is behind it
            shape.setAttached(com.magicbanana666.morphingtime.MorphingTimeAttachments.SHAPE_PLAYER, ((Player) (Object) this).getUUID().toString());
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Unique
    @Override
    public void shape$updateShapes(@Nullable LivingEntity shape) {
        Player player = (Player) (Object) this;
        AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance armorAttribute = player.getAttribute(Attributes.ARMOR);
        AttributeInstance armorToughnessAttribute = player.getAttribute(Attributes.ARMOR_TOUGHNESS);

        // spawn particles
        if (Shapes.CONFIG.emit_particles && !Objects.equals(ShapeType.from(this.shape$shape), ShapeType.from(shape)) && !player.isSpectator() && player.level() instanceof ServerLevel l) {
            l.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1, player.getZ(), 25, .5, 1.0, .5, .75);
            l.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1, player.getZ(), 25, .5, 1.0, .5, .75);
            l.sendParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getY() + 1, player.getZ(), 25, .5, 1.0, .5, .7);
        }

        boolean firstShape = this.shape$shape == null;
        this.shape$shape = shape;

        // shape is valid and scaling health is on; set entity's max health and current
        // health to reflect shape.
        if (shape != null) {
            if (!Shapes.CONFIG.show_on_locator_bar && player instanceof ServerPlayer) {
                AttributeInstance waypointTransmitRange = this.getAttribute(Attributes.WAYPOINT_TRANSMIT_RANGE);
                if (waypointTransmitRange != null) {
                    waypointTransmitRange.addOrReplacePermanentModifier(WAYPOINT_TRANSMIT_MODIFIER);
                }
            }

            if (Shapes.CONFIG.scalingHealth && healthAttribute != null) {
                if (firstShape) { // only cache health when in first shape
                    this.shape$normalHealth = (float) healthAttribute.getBaseValue();
                }

                // calculate the current health in percentage, used later
                float currentHealthPercent = player.getHealth() / player.getMaxHealth();

                AttributeInstance shapeHealthAttribute = shape.getAttribute(Attributes.MAX_HEALTH);
                healthAttribute.setBaseValue(Math.min(Shapes.CONFIG.maxHealth, shapeHealthAttribute.getBaseValue()));

                // set health
                if (Shapes.CONFIG.percentScalingHealth) {
                    player.setHealth(Math.min(currentHealthPercent * player.getMaxHealth(), player.getMaxHealth()));
                } else {
                    player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
                }
            }
            if (Shapes.CONFIG.scalingAmor) {
                AttributeInstance shapeArmorAttribute = shape.getAttribute(Attributes.ARMOR);
                if (armorAttribute != null && shapeArmorAttribute != null) {
                    armorAttribute.setBaseValue(Math.min(Shapes.CONFIG.maxAmor, shapeArmorAttribute.getBaseValue()));
                }
                AttributeInstance shapeArmorToughnessAttribute = shape.getAttribute(Attributes.ARMOR_TOUGHNESS);
                if (armorToughnessAttribute != null && shapeArmorToughnessAttribute != null) {
                    armorToughnessAttribute.setBaseValue(Math.min(Shapes.CONFIG.maxAmorToughness, shapeArmorToughnessAttribute.getBaseValue()));
                }
            }

            AttributeInstance playerScaleAttribute = player.getAttribute(Attributes.SCALE);
            AttributeInstance shapeScaleAttribute = shape.getAttribute(Attributes.SCALE);
            if (playerScaleAttribute != null && shapeScaleAttribute != null) {
                shapeScaleAttribute.setBaseValue(playerScaleAttribute.getBaseValue());
            }

            if (Shapes.CONFIG.scalingStepHeight) {
                AttributeInstance playerStepHeightAttr = player.getAttribute(Attributes.STEP_HEIGHT);

                if (playerStepHeightAttr != null) {
                    playerStepHeightAttr.setBaseValue(shape.maxUpStep());
                }
            }
        }

        // Attack damage, speed and the other stats of the mob
        ShapeStats.update(player, shape);

        // refresh entity hitbox dimensions
        player.refreshDimensions();

        // If the shape is null (going back to player), set the player's base health
        // value to 20 (default) to clear old changes.
        if (shape == null) {
            AttributeInstance waypointTransmitRange = this.getAttribute(Attributes.WAYPOINT_TRANSMIT_RANGE);
            if (waypointTransmitRange != null) {
                waypointTransmitRange.removeModifier(WAYPOINT_TRANSMIT_MODIFIER);
            }

            if (Shapes.CONFIG.scalingHealth && healthAttribute != null) {
                float currentHealthPercent = player.getHealth() / player.getMaxHealth();

                float health = Shapes.CONFIG.player_health;
                if (health < 0) {
                    if (shape$normalHealth > 0) {
                        health = shape$normalHealth;
                    } else {
                        health = 20;
                    }
                }
                healthAttribute.setBaseValue(health);

                // Clear health value if needed
                if (Shapes.CONFIG.percentScalingHealth) {

                    player.setHealth(Math.min(currentHealthPercent * player.getMaxHealth(), player.getMaxHealth()));
                } else {
                    player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
                }
            }

            if (Shapes.CONFIG.scalingAmor) {
                if (armorAttribute != null) {
                    armorAttribute.setBaseValue(0);
                }
                if (armorToughnessAttribute != null) {
                    armorToughnessAttribute.setBaseValue(0);
                }
            }

            if (Shapes.CONFIG.scalingStepHeight) {
                AttributeInstance playerStepHeightAttr = player.getAttribute(Attributes.STEP_HEIGHT);

                if (playerStepHeightAttr != null) {
                    playerStepHeightAttr.setBaseValue(0.6);
                }
            }
        }

        // update flight properties on player depending on shape
        ServerPlayer serverPlayer = (ServerPlayer) player;
        if (Shapes.hasFlyingPermissions((ServerPlayer) player)) {
            FlightHelper.grantFlightTo(serverPlayer);
            FlightHelper.updateFlyingSpeed(player);
            player.onUpdateAbilities();
        } else if (!player.isCreative()) {
            FlightHelper.revokeFlight(serverPlayer);
            player.getAbilities().setFlyingSpeed(0.05f);
            player.onUpdateAbilities();
        }

        // If the player is riding a Ravager and changes into a shape that cannot
        // ride Ravagers, kick them off.
        if (player.getVehicle() instanceof LivingEntity livingVehicle) {
            // Check if the player can continue riding
            boolean b1 = false;
            boolean b2 = false;
            for (RiderTrait<?> riderTrait : TraitRegistry.get(shape, RiderTrait.ID).stream().map(entry -> (RiderTrait<?>) entry).toList()) {
                if (riderTrait.isRideable(livingVehicle) || (livingVehicle instanceof Player rideablePlayer && riderTrait.isRideable(PlayerShape.getCurrentShape(rideablePlayer)))) {
                    b1 = true;
                    b2 = true;
                }
                if (b2) break;
            }
            if (!b1) {
                player.stopRiding();
            }
        }

        // If the player is riding another Player that switches into another shape that cannot
        // be ridden, the riding player stops riding
        if (!(shape instanceof AbstractHorse)) {
            for (Entity passenger : player.getPassengers()) {
                passenger.stopRiding();
            }
        }

        // sync with client
        if (!player.level().isClientSide()) {
            PlayerShape.sync((ServerPlayer) player);

            Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) ((ServerLevel) player.level())
                    .getChunkSource().chunkMap).getEntityMap();
            Object tracking = trackers.get(player.getId());
            // Fake players (EG from machines of other mods) aren't tracked
            if (tracking != null) {
                ((EntityTrackerAccessor) tracking).getSeenBy().forEach(
                        listener -> PlayerShape.sync((ServerPlayer) player, listener.getPlayer())
                );
            }
        }
    }

    @Override
    public void shape$setIsSpecialAnim(boolean specialAnimation) {
        this.shape$specialAnimation = specialAnimation;
    }

    @Override
    public boolean shape$getIsSpecialAnim() {
        return this.shape$specialAnimation;
    }
}
