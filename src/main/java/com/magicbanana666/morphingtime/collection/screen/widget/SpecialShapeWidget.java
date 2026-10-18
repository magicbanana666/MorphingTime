package com.magicbanana666.morphingtime.collection.screen.widget;

import com.magicbanana666.morphingtime.collection.MorphCollection;
import com.magicbanana666.morphingtime.collection.network.NetworkHandler;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import com.magicbanana666.morphingtime.shape.api.variant.TypeProvider;
import com.magicbanana666.morphingtime.shape.api.variant.TypeProviderRegistry;
import com.magicbanana666.morphingtime.shape.network.impl.SwapVariantPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;


public class SpecialShapeWidget extends AbstractButton {
    private final Screen parent;
    private final boolean isCurrent;
    private final boolean isAvailable;

    public SpecialShapeWidget(int x, int y, int width, int height, Screen parent) {
        super(x, y, width, height, Component.nullToEmpty(""));
        this.parent = parent;

        // check if current shape is the special shape
        assert Minecraft.getInstance().level != null;
        TagValueOutput out = TagValueOutput.createWithContext(Shapes.PROBLEM_REPORTER, Minecraft.getInstance().level.registryAccess());
        if (Minecraft.getInstance().player != null && PlayerShape.getCurrentShape(Minecraft.getInstance().player) instanceof Wolf wolf) {
            wolf.saveWithoutId(out);
        }
        this.isCurrent = out.buildResult().getBooleanOr("isSpecial", false);
        this.isAvailable = Minecraft.getInstance().player != null && MorphCollection.canUseShape(Minecraft.getInstance().player, EntityTypes.WOLF);
        setTooltip(Tooltip.create(Component.translatable(isAvailable ? "remorphed.special_shape_available" : "remorphed.special_shape_unavailable")));
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MorphCollection.id("textures/gui/wolf.png"), getX(), getY(), 0, 0, getWidth(), getHeight(), 15, 15, 15, 15);

        if (!isCurrent && !isAvailable) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MorphCollection.id("textures/gui/unavailable.png"), getX(), getY(), 0, 0, getWidth(), getHeight(), 15, 15, 15, 15);
        }

        // Highlight when focused
        if (isHoveredOrFocused()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MorphCollection.id("textures/gui/head_focus.png"), getX(), getY(), 0, 0, getWidth(), getHeight(), 16, 16, 16, 16);
        }
    }

    @Override
    public void onPress(@NotNull InputWithModifiers input) {
        UUID profileId = Minecraft.getInstance().getUser().getProfileId();
        if (!isCurrent && isAvailable && Shapes.hasSpecialShape(profileId)) {
            // get variant range
            TypeProvider<Wolf> typeProvider = TypeProviderRegistry.getProvider(EntityTypes.WOLF);
            int range = typeProvider != null ? typeProvider.size(Minecraft.getInstance().level) : -1;
            // swap to variant
            NetworkHandler.sendSwap2ndShapeRequest(Objects.requireNonNull(ShapeType.from(EntityTypes.WOLF, -1)));
            SwapVariantPackets.sendSwapRequest(range);
            parent.onClose();
        }
    }

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput builder) {

    }
}
