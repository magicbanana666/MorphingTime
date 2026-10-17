package com.magicbanana666.morphingtime.shape.traits;

import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.ShapesClient;
import com.magicbanana666.morphingtime.shape.ability.ShapeAbility;
import com.magicbanana666.morphingtime.shape.traits.impl.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-side look of traits and abilities: the badge icon, its colour category and the tooltip text.
 * The same data drives the badges on the morph cards and the icon legend.
 */
public final class TraitIcons {
    public static final int SIZE = 12;

    public enum Category {
        ABILITY("ability", 0xFFEEBE45),
        STRENGTH("strength", 0xFF5FD38A),
        WEAKNESS("weakness", 0xFFF06A64),
        NEUTRAL("neutral", 0xFF73A6EE);

        public final String name;
        public final int textColor;

        Category(String name, int textColor) {
            this.name = name;
            this.textColor = textColor;
        }

        public Identifier badge() {
            return texture("badge_" + name);
        }

        public Component title() {
            return Component.translatable("walkers.legend.category." + name);
        }
    }

    /**
     * @param texture finished badge texture, or null to draw {@code effect} on top of the category badge
     */
    public record Icon(@Nullable Identifier texture, @Nullable Holder<MobEffect> effect, Category category, Component name, Component description) {
        public void draw(GuiGraphicsExtractor graphics, int x, int y, int size) {
            if (texture != null) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, size, size, SIZE, SIZE, SIZE, SIZE);
            } else {
                graphics.blit(RenderPipelines.GUI_TEXTURED, category.badge(), x, y, 0, 0, size, size, SIZE, SIZE, SIZE, SIZE);
                if (effect != null) {
                    int inset = size / SIZE;
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Gui.getMobEffectSprite(effect), x + inset, y + inset, size - 2 * inset, size - 2 * inset);
                }
            }
        }

        public List<Component> tooltip() {
            List<Component> lines = new ArrayList<>();
            lines.add(name.copy().withColor(category.textColor));
            lines.add(description.copy().withStyle(ChatFormatting.GRAY));
            return lines;
        }
    }

    private TraitIcons() {
    }

    public static Identifier texture(String name) {
        return Shapes.id("textures/gui/traits/" + name + ".png");
    }

    private static Icon simple(String key, Category category, Object... args) {
        return new Icon(texture(key), null, category, Component.translatable("walkers.trait." + key), Component.translatable("walkers.trait." + key + ".desc", args));
    }

    /**
     * @return the icon for this trait, or null if the trait shouldn't show one
     */
    public static @Nullable Icon of(@NotNull ShapeTrait<?> trait) {
        return switch (trait) {
            case FlyingTrait<?> _ -> simple("flying", Category.STRENGTH);
            case ClimbBlocksTrait<?> _ -> simple("climb_blocks", Category.STRENGTH);
            case SlowFallingTrait<?> _ -> simple("slow_falling", Category.STRENGTH);
            case StandOnFluidTrait<?> _ -> simple("stand_on_fluid", Category.STRENGTH);
            case WalkOnPowderSnow<?> _ -> simple("walk_on_powder_snow", Category.STRENGTH);
            case CantFreezeTrait<?> _ -> simple("cant_freeze", Category.STRENGTH);
            case InvulnerabilityTrait<?> _ -> simple("invulnerability", Category.STRENGTH);
            case ImmunityTrait<?> t -> simple("immunity", Category.STRENGTH, t.effect.getDisplayName());
            case NoPhysicsTrait<?> _ -> simple("no_physics", Category.STRENGTH);
            case UndrownableTrait<?> _ -> simple("undrownable", Category.STRENGTH);
            case ReinforcementsTrait<?> _ -> simple("reinforcements", Category.STRENGTH);
            case AttackForHealthTrait<?> _ -> simple("attack_for_health", Category.STRENGTH);
            case FearedTrait<?> _ -> simple("feared", Category.STRENGTH);
            case BurnInDaylightTrait<?> t -> t.burnInMoonlightInstead
                    ? simple("burn_in_moonlight", Category.WEAKNESS)
                    : simple("burn_in_daylight", Category.WEAKNESS);
            case CantSwimTrait<?> _ -> simple("cant_swim", Category.WEAKNESS);
            case PreyTrait<?> _ -> simple("prey", Category.WEAKNESS);
            case CantInteractTrait<?> _ -> simple("cant_interact", Category.WEAKNESS);
            case InstantDieOnDamageMsgTrait<?> t -> simple("instant_die_on_damage_msg", Category.WEAKNESS, damageName(t.msgId));
            case TemperatureTrait<?> t -> new Icon(texture("temperature"), null, Category.WEAKNESS,
                    Component.translatable("walkers.trait.temperature"),
                    Component.translatable(t.coldEnoughToSnow ? "walkers.trait.temperature.desc_cold" : "walkers.trait.temperature.desc_warm"));
            case AquaticTrait<?> t -> {
                if (!t.isAquatic) yield null; // plain land mob, nothing to show
                yield new Icon(texture("aquatic"), null, t.isLand ? Category.STRENGTH : Category.NEUTRAL,
                        Component.translatable("walkers.trait.aquatic"),
                        Component.translatable(t.isLand ? "walkers.trait.aquatic.desc_amphibious" : "walkers.trait.aquatic.desc"));
            }
            case NocturnalTrait<?> _ -> simple("nocturnal", Category.NEUTRAL);
            case RiderTrait<?> _ -> simple("rider", Category.NEUTRAL);
            case MobEffectTrait<?> t -> mobEffect(t);
            default -> null; // humanoid and unknown traits from add-ons
        };
    }

    /**
     * Whether the trait is one of the built-in ones. Those are all handled by {@link #of(ShapeTrait)}, so when it
     * gives no icon for them it is meant to be hidden.
     */
    public static boolean isBuiltIn(@NotNull ShapeTrait<?> trait) {
        return trait.getClass().getPackageName().equals(FlyingTrait.class.getPackageName());
    }

    /**
     * Readable name of a damage message id (EG "lightningBolt" becomes "lightning bolt"). Can be translated with
     * "walkers.damage.&lt;id&gt;".
     */
    private static Component damageName(String msgId) {
        String readable = msgId.replaceAll("([a-z0-9])([A-Z])", "$1 $2").replace('_', ' ').replace('.', ' ').toLowerCase(java.util.Locale.ROOT);
        return Component.translatableWithFallback("walkers.damage." + msgId, readable);
    }

    private static Icon mobEffect(MobEffectTrait<?> trait) {
        Holder<MobEffect> effect = trait.mobEffectInstance.getEffect();
        boolean harmful = effect.value().getCategory() == MobEffectCategory.HARMFUL;
        MutableComponent name = Component.empty().append(effect.value().getDisplayName());
        if (trait.mobEffectInstance.getAmplifier() > 0) {
            name.append(" ").append(Component.translatable("enchantment.level." + (trait.mobEffectInstance.getAmplifier() + 1)));
        }

        Category category;
        String descKey;
        if (trait.applyToSelf) {
            category = harmful ? Category.WEAKNESS : Category.STRENGTH;
            descKey = "walkers.trait.mob_effect.desc_self";
        } else {
            // handing out a harmful effect to others is a strength
            category = harmful ? Category.STRENGTH : Category.NEUTRAL;
            descKey = "walkers.trait.mob_effect.desc_nearby";
        }
        return new Icon(null, effect, category, name, Component.translatable(descKey, name.copy()));
    }

    public static Icon ability(@NotNull ShapeAbility<?> ability) {
        Identifier id = ability.getId();
        String path = id != null ? id.getPath() : "unknown";
        String fallbackName = Character.toUpperCase(path.charAt(0)) + path.substring(1).replace('_', ' ');
        Component name = Component.translatable("walkers.legend.active", Component.translatableWithFallback("walkers.ability." + path, fallbackName));
        Component description = Component.translatableWithFallback("walkers.ability." + path + ".desc", "")
                .copy().append(Component.translatable("walkers.legend.press", ShapesClient.ABILITY_KEY.getTranslatedKeyMessage()));
        return new Icon(texture("ability"), null, Category.ABILITY, name, description);
    }

    /**
     * Fallback for traits from add-ons: draw their own icon shrunk into a neutral badge.
     */
    public static boolean drawForeign(ShapeTrait<?> trait, GuiGraphicsExtractor graphics, int x, int y, int size) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, Category.NEUTRAL.badge(), x, y, 0, 0, size, size, SIZE, SIZE, SIZE, SIZE);
        graphics.pose().pushMatrix();
        graphics.pose().translate(x + 1, y + 1);
        graphics.pose().scale((size - 2) / 16f, (size - 2) / 16f);
        boolean drawn = trait.renderIcon(RenderPipelines.GUI_TEXTURED, graphics, 0, 0, 16, 16);
        graphics.pose().popMatrix();
        return drawn;
    }

    public static Component foreignName(ShapeTrait<?> trait) {
        Identifier id = trait.getId();
        return Component.translatableWithFallback("walkers.trait." + id.getPath(), id.toString());
    }

    /**
     * Every built-in icon in legend order, so the legend doesn't need an entity to look them up.
     */
    public static List<Icon> legendEntries() {
        List<Icon> icons = new ArrayList<>();
        for (String key : List.of("flying", "climb_blocks", "slow_falling", "stand_on_fluid", "walk_on_powder_snow", "cant_freeze",
                "invulnerability", "no_physics", "undrownable", "reinforcements", "attack_for_health", "feared")) {
            icons.add(simple(key, Category.STRENGTH));
        }
        icons.add(simple("immunity", Category.STRENGTH, Component.translatable("walkers.legend.an_effect")));
        for (String key : List.of("burn_in_daylight", "burn_in_moonlight", "cant_swim", "prey", "cant_interact")) {
            icons.add(simple(key, Category.WEAKNESS));
        }
        icons.add(new Icon(texture("temperature"), null, Category.WEAKNESS, Component.translatable("walkers.trait.temperature"), Component.translatable("walkers.trait.temperature.desc_legend")));
        icons.add(new Icon(texture("instant_die_on_damage_msg"), null, Category.WEAKNESS, Component.translatable("walkers.trait.instant_die_on_damage_msg"), Component.translatable("walkers.trait.instant_die_on_damage_msg.desc_legend")));
        icons.add(new Icon(texture("aquatic"), null, Category.NEUTRAL, Component.translatable("walkers.trait.aquatic"), Component.translatable("walkers.trait.aquatic.desc")));
        icons.add(simple("nocturnal", Category.NEUTRAL));
        icons.add(simple("rider", Category.NEUTRAL));
        return icons;
    }

    public static Icon legendEffect(boolean harmful) {
        Holder<MobEffect> effect = harmful ? MobEffects.POISON : MobEffects.NIGHT_VISION;
        return new Icon(null, effect, harmful ? Category.WEAKNESS : Category.STRENGTH,
                Component.translatable(harmful ? "walkers.legend.effect_bad" : "walkers.legend.effect_good"),
                Component.translatable(harmful ? "walkers.legend.effect_bad.desc" : "walkers.legend.effect_good.desc"));
    }

    public static Icon legendAbility() {
        return new Icon(texture("ability"), null, Category.ABILITY, Component.translatable("walkers.legend.ability"),
                Component.translatable("walkers.legend.ability.desc", ShapesClient.ABILITY_KEY.getTranslatedKeyMessage()));
    }
}
