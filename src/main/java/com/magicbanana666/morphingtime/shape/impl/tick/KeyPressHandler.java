package com.magicbanana666.morphingtime.shape.impl.tick;

import com.magicbanana666.morphingtime.core.event.client.ClientTickEvents;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.ShapesClient;
import com.magicbanana666.morphingtime.shape.ability.AbilityRegistry;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.api.blacklist.EntityBlacklist;
import com.magicbanana666.morphingtime.shape.api.platform.ApiLevel;
import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import com.magicbanana666.morphingtime.shape.impl.PlayerDataProvider;
import com.magicbanana666.morphingtime.shape.network.ClientNetworking;
import com.magicbanana666.morphingtime.shape.network.impl.SwapPackets;
import com.magicbanana666.morphingtime.shape.network.impl.SwapVariantPackets;
import com.magicbanana666.morphingtime.shape.network.impl.UnlockPackets;
import com.magicbanana666.morphingtime.shape.screen.hud.VariantMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class KeyPressHandler implements ClientTickEvents.Client {
    private float currentTimer = 0f;

    @Override
    public void tick(@NotNull Minecraft client) {
        if (client.player != null) {
            if (ShapesClient.ABILITY_KEY.consumeClick()) {
                handleAbilityKey(client);
            }

            if (ShapesClient.TRANSFORM_KEY.consumeClick()) {
                if (ApiLevel.getCurrentLevel().canMorph) {
                    SwapPackets.sendSwapRequest();
                } else {
                    client.player.sendOverlayMessage(Component.translatable("walkers.feature_not_available"));
                }
            }

            if (ShapesClient.VARIANTS_MENU_KEY.consumeClick() && Shapes.CONFIG.unlockEveryVariant) {
                if (ApiLevel.getCurrentLevel().allowVariantsMenu) {
                    LivingEntity shape = PlayerShape.getCurrentShape(client.player);
                    if (shape != null) {
                        ShapeType<?> shapeType = ShapeType.from(shape);
                        if (ShapesClient.isRenderingVariantsMenu) {
                            SwapVariantPackets.sendSwapRequest(shapeType.getVariantData() + ShapesClient.variantOffset);
                            VariantMenu.clearEntities();
                        } else if (Shapes.CONFIG.show_variants_menu_guide) {
                            client.player.sendOverlayMessage(Component.translatable("walkers.variants_menu_guide", ShapesClient.VARIANTS_MENU_KEY.getTranslatedKeyMessage()));
                            Shapes.CONFIG.show_variants_menu_guide = false;
                            if (client.isSingleplayer()) {
                                Shapes.CONFIG.save();
                            }
                        }
                        ShapesClient.variantOffset = 0;
                        ShapesClient.isRenderingVariantsMenu = !ShapesClient.isRenderingVariantsMenu;
                    }
                } else {
                    client.player.sendOverlayMessage(Component.translatable("walkers.feature_not_available"));
                }
            }

            // disable variants menu when in other menu
            if (ShapesClient.isRenderingVariantsMenu && (client.options.hideGui || !Shapes.CONFIG.unlockEveryVariant || client.screen != null || PlayerShape.getCurrentShape(client.player) == null))
                ShapesClient.isRenderingVariantsMenu = false;

            if (ShapesClient.UNLOCK_KEY.isDown()) {
                if (ApiLevel.getCurrentLevel().canUnlock) {
                    handleUnlockKey(client);
                } else {
                    client.player.sendOverlayMessage(Component.translatable("walkers.feature_not_available"));
                }
            } else if (currentTimer != Shapes.CONFIG.unlockTimer) currentTimer = Shapes.CONFIG.unlockTimer;
        }
    }

    private void handleAbilityKey(@NotNull Minecraft client) {
        LivingEntity shape = PlayerShape.getCurrentShape(client.player);

        if (shape != null) {
            if (AbilityRegistry.has(shape)) {
                ClientNetworking.sendAbilityRequest();
            }
        }
    }

    // TODO: Merge this into something similar to the OnInteractEvent
    @SuppressWarnings("ConstantConditions")
    private void handleUnlockKey(@NotNull Minecraft client) {
        // check if player is blacklisted
        if (Shapes.isPlayerBlacklisted(client.player.getUUID()) && Shapes.CONFIG.blacklistPreventsUnlocking) {
            client.player.sendOverlayMessage(Component.translatable("walkers.player_blacklisted"));
            return;
        }

        HitResult hit = client.hitResult;
        if ((((PlayerDataProvider) client.player).shape$get2ndShape() == null || Shapes.CONFIG.unlockOverridesCurrentShape) && hit instanceof EntityHitResult) {
            Entity entityHit = ((EntityHitResult) hit).getEntity();
            if (entityHit instanceof LivingEntity living) {
                @Nullable ShapeType<?> type = ShapeType.from(living);

                if (type != null) {
                    // Ensure the mob isn't on the blacklist
                    if (EntityBlacklist.isBlacklisted(type.getEntityType()))
                        client.player.sendOverlayMessage(Component.translatable("walkers.unlock_entity_blacklisted"));
                    else {
                        if (currentTimer <= 0) {
                            // unlock shape
                            UnlockPackets.sendUnlockRequest(type);
                            // send unlock message
                            Component name = Component.translatable(type.getEntityType().getDescriptionId());
                            client.player.sendOverlayMessage(Component.translatable("walkers.unlock_entity", name));
                            currentTimer = Shapes.CONFIG.unlockTimer;
                        } else {
                            client.player.sendOverlayMessage(Component.translatable("walkers.unlock_progress"));
                            currentTimer -= 1;
                        }
                    }
                }
            }
        } else currentTimer = Shapes.CONFIG.unlockTimer;
    }
}
