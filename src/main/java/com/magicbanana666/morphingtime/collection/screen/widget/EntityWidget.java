package com.magicbanana666.morphingtime.collection.screen.widget;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.magicbanana666.morphingtime.collection.MorphCollection;
import com.magicbanana666.morphingtime.collection.network.NetworkHandler;
import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import com.magicbanana666.morphingtime.shape.screen.hud.VariantMenu;
import com.magicbanana666.morphingtime.shape.ability.AbilityRegistry;
import com.magicbanana666.morphingtime.shape.ability.ShapeAbility;
import com.magicbanana666.morphingtime.shape.traits.ShapeTrait;
import com.magicbanana666.morphingtime.shape.traits.TraitIcons;
import com.magicbanana666.morphingtime.shape.traits.TraitRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;


public class EntityWidget<T extends LivingEntity> extends ShapeWidget {

    private final ShapeType<T> type;
    private final T entity;
    private final int size;
    private final List<Badge> badges;

    public EntityWidget(int x, int y, int width, int height, ShapeType<T> type, @NotNull T entity, Screen parent, boolean isFavorite, boolean current, int availability) {
        super(x, y, width, height, parent, isFavorite, current, availability);
        this.size = (int) (MorphCollection.CONFIG.entity_size * (1 / (Math.max(entity.getBbHeight(), entity.getBbWidth()))));
        this.type = type;
        this.entity = entity;
        entity.setGlowingTag(true);
        this.badges = collectBadges();
    }

    @Override
    protected void sendFavoriteRequest(boolean isFavorite) {
        NetworkHandler.sendFavoriteRequest(type.getEntityType(), isFavorite);
    }

    @Override
    protected void sendSwap2ndShapeRequest() {
        NetworkHandler.sendSwap2ndShapeRequest(type);
    }

    @Override
    protected Component displayName() {
        return ShapeType.createTooltipText(entity);
    }

    @Override
    protected List<Badge> badges() {
        return badges;
    }

    /**
     * The ability first, then strengths, weaknesses and the rest. Duplicate traits are only shown once, unless their icon differs.
     */
    private List<Badge> collectBadges() {
        List<Badge> result = new ArrayList<>();
        ShapeAbility<T> ability = AbilityRegistry.get(entity);
        if (ability != null) {
            result.add(new Badge(TraitIcons.ability(ability), null));
        }

        List<Badge> traitBadges = new ArrayList<>();
        Set<Identifier> seen = new HashSet<>();
        Set<String> seenIcons = new HashSet<>();
        for (ShapeTrait<T> trait : TraitRegistry.getAll(entity)) {
            if (trait == null) continue;
            try {
                TraitIcons.Icon icon = TraitIcons.of(trait);
                if (icon != null) {
                    // Same trait with another effect or damage (EG two immunities) gives another icon, keep both
                    if (seenIcons.add(icon.texture() + "|" + icon.effect() + "|" + icon.name().getString() + "|" + icon.description().getString())) {
                        traitBadges.add(new Badge(icon, null));
                    }
                } else if (!TraitIcons.isBuiltIn(trait) && (seen.add(trait.getId()) || trait.iconMightDiffer()) && hasOwnIcon(trait)) {
                    // Only add-on traits draw their own icon, built-in traits without an icon are hidden on purpose
                    traitBadges.add(new Badge(null, trait));
                }
            } catch (Exception e) {
                MorphCollection.LOGGER.error("Error while preparing trait icon {} for {}", trait.getId(), EntityType.getKey(entity.getType()), e);
            }
        }
        traitBadges.sort(Comparator.comparingInt(badge -> badge.icon() != null ? badge.icon().category().ordinal() : TraitIcons.Category.NEUTRAL.ordinal()));
        result.addAll(traitBadges);
        return result;
    }

    private static boolean hasOwnIcon(ShapeTrait<?> trait) {
        try {
            return trait.getClass().getMethod("renderIcon", RenderPipeline.class, GuiGraphicsExtractor.class, int.class, int.class, int.class, int.class).getDeclaringClass() != ShapeTrait.class;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @Override
    protected void extractShape(GuiGraphicsExtractor guiGraphics) {
        // Some entities (namely Aether mobs) crash when rendered in a GUI.
        // Unsure as to the cause, but this try/catch should prevent the game from entirely dipping out.
        try {
            int leftPos = (int) (getX() + (float) this.getWidth() / 2);
            int topPos = (int) (getY() + (this.getHeight() - badgeStripHeight()) * .75f);
            int k = leftPos - 20;
            int l = topPos - 25;
            int m = leftPos + 20;
            int n = topPos + 35;
            VariantMenu.renderEntityOnScreen(guiGraphics, k, l, m, n, (float) size,
                    new Vector3f(), new Quaternionf().rotationXYZ(0.43633232F, (float) Math.PI, (float) Math.PI),
                    null, entity);
        } catch (Exception e) {
            MorphCollection.LOGGER.error("Error while rendering {}", ShapeType.createTooltipText(entity).getString(), e);
            setCrashed();
            MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
            immediate.endBatch();
            RenderSystem.getModelViewStack().popMatrix();
        }
    }

    @Override
    void sendDeleteShapePacket() {
        NetworkHandler.sendDeleteShapePacket(type.getEntityType());
    }
}
