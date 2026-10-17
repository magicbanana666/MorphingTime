package com.magicbanana666.morphingtime.shape.api.platform;

import com.magicbanana666.morphingtime.core.config.Config;
import com.magicbanana666.morphingtime.core.config.annotions.Comment;
import com.magicbanana666.morphingtime.core.config.annotions.Synchronize;
import com.magicbanana666.morphingtime.shape.Shapes;

import java.util.*;

@SuppressWarnings("CanBeFinal")
public class MorphConfig implements Config {

    @Comment("Only change this if you know what you're doing! This overrides the server-side API level and can cause addons to stop working properly. -1 - do not override, 0 - API only, 1 - unlocking only, 2 - morphing only, 3 - morphing and variants menu only, 4 - everything")
    public int apiLevel = -1; // no config synchronization because it is synced via the Api Level sync packet
    @Comment("Whether a player's equipped shape is revoked on death.")
    public boolean revoke2ndShapeOnDeath = false;
    @Comment("Whether shapes equip the items (swords, items, tools) held by the underlying player.")
    public boolean shapesEquipItems = true;
    @Comment("Whether shapes equip the armor (chestplate, leggings, elytra) worn by the underlying player.")
    public boolean shapesEquipArmor = true;
    @Comment("If true, the player can cause hostiles to attack via right-clicking.")
    public boolean playerCanTriggerHostiles = true;
    @Comment("Whether hostile mobs ignore players with hostile mob shapes.")
    public boolean hostilesIgnoreHostileShapedPlayer = true;
    @Comment("Whether hostile mobs ignore players, even if the player shapes into a not-hostile mob. Requires 'hostilesIgnoreHostileShapedPlayer' to be 'true'.")
    public boolean hostilesIgnoreNotHostileShapedPlayer = false;
    @Comment("Whether a hostile mob will stop targeting you after switching to a (hostile) mob shape.")
    public boolean hostilesForgetNewShapedPlayer = false;
    @Comment("Whether hunters (e.g. wolves) attack players whose shape is their normal prey (e.g. a sheep).")
    public boolean hunterAttackAsPreyMorphedPlayer = true;
    @Comment("If false, owned hunters (e.g. tamed wolves) don't hunt players whose shape is their prey. Currently has no effect.")
    public boolean ownedHunterAttackAsPreyMorphedPlayer = false;
    @Comment("Whether villagers run from players whose shape villagers normally run from (e.g. zombies).")
    public boolean villagersRunFrom2ndShapes = true;
    @Comment("Whether shape sounds take priority over player sounds (e.g. the blaze hurt sound when hit).")
    public boolean useShapeSounds = true;
    @Comment("Whether disguised players should randomly emit the ambient sound of their shape.")
    public boolean playAmbientSounds = true;
    @Comment("Whether disguised players should hear their own ambient sounds (only if playAmbientSounds is true).")
    public boolean hearSelfAmbient = false;
    @Comment("Whether mobs in the flying entity tag can fly.")
    public boolean enableFlight = true;
    @Comment("How long, in ticks, hostility lasts for players morphed as hostile mobs after attacking a hostile mob.")
    public int hostilityTime = 20 * 15;
    @Comment("A list of advancements required before the player can fly using a shape.")
    public List<String> advancementsRequiredForFlight = new ArrayList<>();
    @Comment("Whether shapes change your max health to match the max health of the mob.")
    public boolean scalingHealth = true;
    @Comment("Keeps the same health percentage when your max health changes. Only works with scalingHealth.")
    public boolean percentScalingHealth = true;
    @Comment("The maximum health a shape can give. Mobs with more health (EG an iron golem with 100) are capped to this.")
    public int maxHealth = 40;
    @Comment("Whether the player should have the same default armor values as the mob.")
    public boolean scalingAmor = true;
    @Comment("The maximum value of default armor. Prevents players from becoming nearly immune to damage.")
    public int maxAmor = 30;
    @Comment("The maximum value of default armor toughness. Prevents players from becoming nearly immune to damage.")
    public int maxAmorToughness = 20;
    @Synchronize
    @Comment("Whether you get the other stats (attributes) of the mob you morph into, see copiedStats. Works for modded mobs too.")
    public boolean copyMobStats = true;
    @Synchronize
    @Comment("The stats taken from the mob, with the highest value you can get from it (-1 = no limit). Any attribute id works, also ones added by other mods. Stats the mob doesn't have (EG attack damage of a cow) stay as they are.")
    public Map<String, Double> copiedStats = new LinkedHashMap<>() {
        {
            put("minecraft:attack_damage", 20.0);
            put("minecraft:attack_knockback", 5.0);
            put("minecraft:knockback_resistance", 1.0);
            put("minecraft:explosion_knockback_resistance", 1.0);
            put("minecraft:movement_speed", 0.2);
            put("minecraft:movement_efficiency", 1.0);
            put("minecraft:water_movement_efficiency", 1.0);
            put("minecraft:jump_strength", 1.0);
            put("minecraft:safe_fall_distance", 64.0);
            put("minecraft:fall_damage_multiplier", -1.0);
            put("minecraft:gravity", -1.0);
            put("minecraft:burning_time", -1.0);
            put("minecraft:oxygen_bonus", -1.0);
        }
    };
    @Comment("Show the morph animation: the old model turns into the new one block by block.")
    public boolean morphAnimation = true;
    @Comment("How long the morph animation takes, in ticks (20 ticks = 1 second).")
    public int morphAnimationTicks = 20;
    @Comment("Show the unlock animation: a glowing copy of a mob you unlock flies into you.")
    public boolean unlockAnimation = true;
    @Synchronize
    @Comment("Mobs measure walking speed differently than players: the mob's movement speed is multiplied by this (a zombie has 0.23, which gives 0.092; a player walks at 0.1).")
    public double movementSpeedFactor = 0.4;
    @Comment("Whether the player's step height should match the step height of the mob.")
    public boolean scalingStepHeight = true;
    @Comment("How far, in blocks, the enderman ability can teleport.")
    public int endermanAbilityTeleportDistance = 32;
    @Comment("Should player nametags render above players disguised with a shape? Note that the server is the authority for this config option.")
    @Synchronize
    public boolean showPlayerNametag = false;
    @Comment("The default fly speed for transformed players.")
    public float flySpeed = 0.05f;
    @Comment("If true, players with the Warden shape will have a shorter view range with the darkness effect.")
    public boolean wardenIsBlinded = true;
    @Comment("If true, players with the Warden shape will blind other nearby players.")
    public boolean wardenBlindsNearby = true;
    @Comment("If true, unlocking a shape replaces the current second shape.")
    @Synchronize
    public boolean unlockOverridesCurrentShape = false;
    @Comment("How long, in ticks, the unlock key must be held while looking at a mob to unlock it.")
    @Synchronize
    public float unlockTimer = 60f;
    @Comment("Allows players to unlock every variant of an entity type at once. Must be true for the variants menu to be available.")
    @Synchronize
    public boolean unlockEveryVariant = true;

    @Comment("The ability cooldown, in ticks, for each mob.")
    public Map<String, Integer> abilityCooldownMap = new HashMap<>() {
        {
            put("minecraft:bee", 20);
            put("minecraft:blaze", 20);
            put("minecraft:camel", 40);
            put("minecraft:chicken", 1200);
            put("minecraft:cow", 20);
            put("minecraft:creeper", 100);
            put("minecraft:ender_dragon", 20);
            put("minecraft:enderman", 100);
            put("minecraft:endermite", 20);
            put("minecraft:evoker", 10);
            put("minecraft:ghast", 60);
            put("minecraft:goat", 20);
            put("minecraft:horse", 40);
            put("minecraft:illusioner", 2400);
            put("minecraft:iron_golem", 20);
            put("minecraft:llama", 20);
            put("minecraft:mooshroom", 300);
            put("minecraft:mule", 40);
            put("minecraft:pillager", 2400);
            put("minecraft:polar_bear", 20);
            put("minecraft:pufferfish", 20);
            put("minecraft:rabbit", 40);
            put("minecraft:ravager", 2400);
            put("minecraft:sheep", 20);
            put("minecraft:shulker", 80);
            put("minecraft:skeleton_horse", 40);
            put("minecraft:sniffer", 9600);
            put("minecraft:snow_golem", 10);
            put("minecraft:trader_llama", 20);
            put("minecraft:turtle", 6000);
            put("minecraft:vindicator", 2400);
            put("minecraft:warden", 200);
            put("minecraft:witch", 200);
            put("minecraft:wither", 200);
            put("minecraft:wolf", 20);
            put("minecraft:zombie_horse", 40);
            put("minecraft:zombified_piglin", 20);
        }
    };

    @Comment("How villager variants (type and profession) are picked. 0 - none, 1 - completely random, 2 - biome based")
    public int multiVectorVariants = 2;

    @Comment("Blacklist entity types (e.g. minecraft:blaze) to disable abilities for the specified mob.")
    @Synchronize
    public List<String> abilityBlacklist = new ArrayList<>() {
        {
            add("minecraft:snow_golem");
        }
    };
    @Comment("Map entity types to a list of traits to disable the traits for the specified entity type.")
    @Synchronize
    public Map<String, List<String>> traitBlacklist = new HashMap<>() {
        {
            put("example:my_mob", List.of("example:first_trait", "example:second_trait"));
        }
    };
    @Comment("Blacklist entity types (e.g. minecraft:blaze) to disable morphing into those.")
    @Synchronize
    public Set<String> entityBlacklist = new HashSet<>();
    @Comment("True - the entity blacklist will be treated as a whitelist. This can be modified via commands.")
    @Synchronize
    public boolean entityBlacklistIsWhitelist = false;
    @Comment("Blacklist players by UUID so they can't unlock shapes. This can be modified via commands.")
    @Synchronize
    public List<UUID> playerUUIDBlacklist = new ArrayList<>();
    @Comment("True - the player blacklist will be treated as a whitelist. This can be modified via commands.")
    @Synchronize
    public boolean playerBlacklistIsWhitelist = false;
    @Comment("Whether the player blacklist should prevent unlocking.")
    @Synchronize
    public boolean blacklistPreventsUnlocking = true;
    @Comment("Whether the player blacklist should prevent morphing.")
    @Synchronize
    public boolean blacklistPreventsMorphing = true;
    @Comment("Block equipment slots per entity type. Use '*' to select all entity types. Valid slots are: head, chest, legs, feet, offhand")
    @Synchronize
    public Map<String, List<String>> blockEquipmentSlots = new HashMap<>() {
        {
            put("minecraft:wolf", List.of("head", "legs", "feet", "offhand"));
        }
    };
    @Comment("Show info on how the variants menu works.")
    public boolean show_variants_menu_guide = true;
    @Comment("Emit particles when morphing.")
    public boolean emit_particles = true;
    @Comment("The max health a player has without a shape (e.g. 20). Set to -1 to use the value cached before morphing.")
    public float player_health = -1;
    @Comment("Whether disguised players are shown on the locator bar.")
    public boolean show_on_locator_bar = false;

    @Override
    public String getName() {
        return Shapes.MODID;
    }
}
