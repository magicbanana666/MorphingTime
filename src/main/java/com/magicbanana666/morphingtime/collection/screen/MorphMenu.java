package com.magicbanana666.morphingtime.collection.screen;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Window;
import com.magicbanana666.morphingtime.collection.MorphCollection;
import com.magicbanana666.morphingtime.collection.compatibility.SkinAccess;
import com.magicbanana666.morphingtime.collection.impl.PlayerMorph;
import com.magicbanana666.morphingtime.collection.mixin.client.accessor.ScreenAccessor;
import com.magicbanana666.morphingtime.collection.screen.widget.*;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("unchecked")

public class MorphMenu extends Screen {
    @Nullable
    protected ShapeListWidget list;
    public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private static String lastSearchContents = "";

    private final List<ShapeType<?>> unlockedShapes = new CopyOnWriteArrayList<>();
    private final List<GameProfile> unlockedSkins = new CopyOnWriteArrayList<>();
    private final Map<ShapeType<?>, Mob> renderEntities = new ConcurrentHashMap<>();
    private final Map<GameProfile, PlayerSkin> renderPlayers = new ConcurrentHashMap<>();

    private final PlayerModel MODEL_WIDE = new PlayerModel(minecraft.getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
    private final PlayerModel MODEL_SLIM = new PlayerModel(minecraft.getEntityModels().bakeLayer(ModelLayers.PLAYER), true);


    private final SearchWidget searchBar = createSearchBar();
    private final Button helpButton = createHelpButton();
    private final PlayerWidget playerButton = createPlayerButton();
    private final SpecialShapeWidget specialShapeButton = createSpecialShapeButton();
    private final Button traitsButton = createTraitsButton();
    private final Button legendButton = createLegendButton();

    public MorphMenu() {
        super(Component.literal("Morphing Time"));
    }

    protected void init() {
        this.addHeader();
        this.addContents();
        this.addFooter();
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    protected void addHeader() {
        LinearLayout linearLayout = this.layout.addToHeader(LinearLayout.horizontal()).spacing(8);

        linearLayout.addChild(traitsButton);
        linearLayout.addChild(legendButton);

        linearLayout.addChild(searchBar);
        linearLayout.addChild(helpButton);
        linearLayout.addChild(playerButton);

        if (minecraft.player != null && Shapes.hasSpecialShape(minecraft.player.getUUID())) {
            linearLayout.addChild(specialShapeButton);
        }
    }

    protected void addContents() {
        this.list = this.layout.addToContents(new ShapeListWidget(this.minecraft, this.width, this.layout));

        if (minecraft.player != null) {
            populateUnlockedRenderEntities(minecraft.player);

            ShapeType<? extends LivingEntity> currentShape = ShapeType.from(PlayerShape.getCurrentShape(minecraft.player));

            // handle favorites
            unlockedShapes.sort((first, second) -> {
                // sort by selected
                if (MorphCollection.CONFIG.sort_selected) {
                    if (Objects.equals(first, currentShape)) {
                        return -1;
                    } else if (Objects.equals(second, currentShape)) {
                        return 1;
                    }
                }
                // sort by favorite
                boolean firstIsFav = PlayerMorph.getFavoriteShapes(minecraft.player).contains(first.getEntityType());
                boolean secondIsFav = PlayerMorph.getFavoriteShapes(minecraft.player).contains(second.getEntityType());
                if (firstIsFav == secondIsFav) {
                    return 0;
                } else if (firstIsFav) {
                    return -1;
                } else {
                    return 1;
                }
            });


            // filter unlocked
            List<ShapeType<?>> newUnlocked = new ArrayList<>();
            for (ShapeType<?> shapeType : unlockedShapes) {
                if (currentShape == null || shapeType.equals(currentShape) || shapeType.getEntityType() != currentShape.getEntityType() || shapeType.getVariantData() == currentShape.getVariantData()) { // only add the current variant, NOT the default one (additionally)
                    newUnlocked.add(shapeType);
                }
            }

            unlockedShapes.clear();
            unlockedShapes.addAll(newUnlocked);

            if (MorphCollection.skinsEnabled) {
                populateUnlockedRenderPlayers(minecraft.player);
                UUID currentSkin = SkinAccess.getCurrentSkin(minecraft.player);

                unlockedSkins.sort((first, second) -> {
                    // the skin is only the current one while no mob shape is used
                    if (Objects.equals(first.id(), currentSkin) && currentShape == null) {
                        return -1;
                    } else if (Objects.equals(second.id(), currentSkin) && currentShape == null) {
                        return 1;
                    } else {
                        boolean firstIsFav = PlayerMorph.getFavoriteSkinIds(minecraft.player).contains(first.id());
                        boolean secondIsFav = PlayerMorph.getFavoriteSkinIds(minecraft.player).contains(second.id());
                        if (firstIsFav == secondIsFav) {
                            return first.name().compareTo(second.name());
                        } else if (firstIsFav) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });

                List<GameProfile> validUnlocked = MorphCollection.getUnlockedSkins(minecraft.player);
                List<CompletableFuture<Void>> pending = validUnlocked.stream()
                        .filter(p -> !p.id().equals(minecraft.player.getUUID()))
                        .filter(p -> EntityRenderCache.getCachedPlayerSkin(p) == null)
                        .map(EntityRenderCache::cachePlayerSkin)
                        .toList();

                if (!pending.isEmpty()) {
                    CompletableFuture.allOf(pending.toArray(new CompletableFuture[0]))
                            .thenRunAsync(() -> {
                                populateUnlockedRenderPlayers(minecraft.player);
                                // Re-trigger the search responder to rebuild the widget list
                                searchBar.setValue(searchBar.getValue());
                            }, Minecraft.getInstance()); // marshal back to main thread
                }
            }
        }

        // implement search handler and display matching entities
        searchBar.setResponder(text -> {
            // re-filter if the text contents changed
            ((ScreenAccessor) this).getNarratables().removeIf(button -> button instanceof EntityWidget);
            children().removeIf(button -> button instanceof EntityWidget);

            List<ShapeType<?>> filteredShapes = unlockedShapes
                    .stream()
                    .filter(type -> text.isEmpty() || ShapeType.createTooltipText(renderEntities.get(type)).getString().toUpperCase().contains(text.toUpperCase()) || EntityType.getKey(type.getEntityType()).toString().toUpperCase().contains(text.toUpperCase()))
                    .toList();
            List<GameProfile> filteredSkins = unlockedSkins
                    .stream()
                    .filter(skin -> text.isEmpty() || skin.name().toUpperCase().contains(text.toUpperCase()) || skin.id().toString().toUpperCase().contains(text.toUpperCase()))
                    .toList();

            populateShapeWidgets(filteredShapes, filteredSkins);

            lastSearchContents = text;
        });
        searchBar.insertText(lastSearchContents);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        if (unlockedShapes.isEmpty() && unlockedSkins.isEmpty()) {
            int y = this.layout.getHeaderHeight() + this.layout.getContentHeight() / 2 - 10;
            graphics.centeredText(this.font, Component.translatable("remorphed.menu.empty"), this.width / 2, y, MenuStyle.TEXT);
            graphics.centeredText(this.font, Component.translatable("remorphed.menu.empty_hint"), this.width / 2, y + 12, MenuStyle.TEXT_DIM);
        }
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractTransparentBackground(graphics);
    }

    @SuppressWarnings("unchecked")
    private void populateShapeWidgets(@NotNull List<ShapeType<?>> rendered, @NotNull List<GameProfile> skinProfiles) {
        if (this.list != null && minecraft.player != null) {
            this.list.clearEntries();

            // add widget for each entity to be rendered
            int rows = (int) Math.ceil((float) (rendered.size() + skinProfiles.size()) / MorphCollection.CONFIG.shapes_per_row);

            ShapeType<LivingEntity> currentType = ShapeType.from(PlayerShape.getCurrentShape(minecraft.player));
            int currentRow = 0;

            for (int i = 0; i <= rows; i++) {
                List<ShapeWidget> row = new ArrayList<>();

                for (int j = 0; j < MorphCollection.CONFIG.shapes_per_row; j++) {
                    int listIndex = i * MorphCollection.CONFIG.shapes_per_row + j;

                    if (MorphCollection.skinsEnabled && listIndex < skinProfiles.size()) {
                        GameProfile skinProfile = skinProfiles.get(listIndex);
                        PlayerSkin playerSkin = renderPlayers.get(skinProfile);
                        if (playerSkin != null) {
                            boolean bl = Objects.equals(SkinAccess.getCurrentSkin(minecraft.player), skinProfile.id()) && currentType == null;
                            if (bl) currentRow = i;
                            row.add(new SkinWidget(
                                    0,
                                    0,
                                    0,
                                    0,
                                    skinProfile,
                                    playerSkin,
                                    playerSkin.model() == PlayerModelType.WIDE ? MODEL_WIDE : MODEL_SLIM,
                                    this,
                                    PlayerMorph.getFavoriteSkins(minecraft.player).contains(skinProfile),
                                    bl,
                                    MorphCollection.canUseEveryShape(minecraft.player) || MorphCollection.CONFIG.playerKillValue < 1 ? -1 : MorphCollection.CONFIG.playerKillValue * (PlayerMorph.getPlayerKills(minecraft.player, skinProfile.id()) / MorphCollection.CONFIG.killToUnlockPlayers) - PlayerMorph.getCounter(minecraft.player, skinProfile.id())
                            ));
                        } else {
                            MorphCollection.LOGGER.error("invalid skin profile: {}", skinProfile);
                        }
                    } else if (listIndex < skinProfiles.size() + rendered.size()) {
                        ShapeType<?> type = rendered.get(listIndex - skinProfiles.size());
                        EntityType<? extends @NotNull LivingEntity> eType = type.getEntityType();
                        Mob entity = renderEntities.get(type);
                        if (entity != null) {
                            boolean bl = type.equals(currentType);
                            if (bl) currentRow = i;
                            row.add(new EntityWidget<>(
                                    0,
                                    0,
                                    0,
                                    0,
                                    (ShapeType<Mob>) type,
                                    entity,
                                    this,
                                    PlayerMorph.getFavoriteShapes(minecraft.player).contains(type.getEntityType()),
                                    bl,
                                    MorphCollection.canUseEveryShape(minecraft.player) || MorphCollection.getKillValue(type.getEntityType()) < 1 ? -1 : MorphCollection.getKillValue(type.getEntityType()) * (PlayerMorph.getKills(minecraft.player, eType) / MorphCollection.CONFIG.killToUnlock) - PlayerMorph.getCounter(minecraft.player, eType)
                            ));
                        } else {
                            MorphCollection.LOGGER.error("invalid shape type: {}", type.getEntityType().getDescriptionId());
                        }
                    }
                }

                this.list.addRow(row.toArray(ShapeWidget[]::new));
            }

            if (MorphCollection.CONFIG.focus_selected) {
                // auto center the selected shape
                this.list.setScrollAmount((double) this.list.rowHeight() * (currentRow - 2));
            }
        }
    }

    public synchronized void populateUnlockedRenderEntities(Player player) {
        unlockedShapes.clear();
        renderEntities.clear();

        List<EntityType<?>> validUnlocked = MorphCollection.getUnlockedShapes(player);

        for (EntityType<?> type : validUnlocked) {
            // Try to get from global cache first
            ShapeType<?> shapeType = PlayerMorph.getDefaultVariants(player).get(type);
            if (shapeType == null) shapeType = ShapeType.from((EntityType<? extends @NotNull LivingEntity>) type);
            EntityRenderCache.CachedEntityData cachedData = EntityRenderCache.getCachedEntity(shapeType);

            if (cachedData != null && cachedData.entity() instanceof Mob cachedMob) {
                // Cache hit! Use the pre-loaded entity
                renderEntities.put(shapeType, cachedMob);
                unlockedShapes.add(shapeType);
            } else {
                // Cache miss - create, prepare, and cache entity on-demand
                EntityRenderCache.cacheEntity(shapeType, player);

                // Now retrieve the prepared entity from cache
                cachedData = EntityRenderCache.getCachedEntity(shapeType);
                if (cachedData != null && cachedData.entity() instanceof Mob cachedMob) {
                    renderEntities.put(shapeType, cachedMob);
                    unlockedShapes.add(shapeType);
                }
            }
        }
    }

    public synchronized void populateUnlockedRenderPlayers(Player player) {
        unlockedSkins.clear();
        renderPlayers.clear();

        for (GameProfile profile : MorphCollection.getUnlockedSkins(player)) {
            if (profile.id().equals(player.getUUID())) continue;

            EntityRenderCache.CachedPlayerSkin cachedData = EntityRenderCache.getCachedPlayerSkin(profile);
            if (cachedData != null) {
                renderPlayers.put(profile, cachedData.skin());
                unlockedSkins.add(profile);
            }
        }
    }

    /**
     * Clears the entity and player caches. Call this when the player logs out
     * or when you want to force a complete refresh of all entities.
     */
    public static void clearCache() {
        EntityRenderCache.clearCache();
    }


    protected void addFooter() {
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).width(200).build());
    }

    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.list != null) {
            this.list.updateSize(this.width, this.layout);
        }
    }

    @Contract(" -> new")
    private @NotNull SearchWidget createSearchBar() {
        return new SearchWidget(
                0,
                0,
                getWindow().getGuiScaledWidth() / 4f,
                20f);
    }

    private @NotNull Button createHelpButton() {
        Button.Builder helpButton = Button.builder(Component.nullToEmpty("?"), (widget) -> Minecraft.getInstance().setScreen(new MorphHelpScreen()));

        helpButton.size(20, 20);
        helpButton.tooltip(Tooltip.create(Component.translatable(MorphCollection.MODID + ".help")));
        return helpButton.build();
    }

    private static Component traitsButtonText() {
        return Component.translatable(MorphCollection.displayDataInMenu ? "remorphed.show_traits.on" : "remorphed.show_traits.off");
    }

    private @NotNull Button createTraitsButton() {
        Button.Builder traitButton = Button.builder(traitsButtonText(), (widget) -> {
            MorphCollection.displayDataInMenu = !MorphCollection.displayDataInMenu;
            widget.setMessage(traitsButtonText());
        });

        int width = Math.max(Minecraft.getInstance().font.width(Component.translatable("remorphed.show_traits.on")), Minecraft.getInstance().font.width(Component.translatable("remorphed.show_traits.off")));
        traitButton.size(width + 12, 20);
        traitButton.tooltip(Tooltip.create(Component.translatable(MorphCollection.MODID + ".traits")));

        return traitButton.build();
    }

    private @NotNull Button createLegendButton() {
        Component text = Component.translatable("remorphed.legend");
        Button.Builder legendButton = Button.builder(text, (widget) -> Minecraft.getInstance().setScreen(new TraitLegendScreen()));

        legendButton.size(Minecraft.getInstance().font.width(text) + 12, 20);
        legendButton.tooltip(Tooltip.create(Component.translatable("remorphed.legend.tooltip")));
        return legendButton.build();
    }

    @Contract(" -> new")
    private @NotNull PlayerWidget createPlayerButton() {
        return new PlayerWidget(
                0,
                0,
                20,
                20,
                this);
    }

    @Contract(" -> new")
    private @NotNull SpecialShapeWidget createSpecialShapeButton() {
        return new SpecialShapeWidget(
                0,
                0,
                20,
                20,
                this);
    }

    private @NotNull Window getWindow() {
        return Minecraft.getInstance().getWindow();
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        for (GuiEventListener child : children()) {
            if (child.keyPressed(event)) {
                return true;
            }
        }
        return super.keyPressed(event);
    }
}
