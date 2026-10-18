# Morphing Time

**Kill a mob, become the mob.** Morphing Time lets you unlock every mob you defeat and morph into it:
you get its shape, its size, its health and its abilities. Fly as a bat, climb walls as a spider, breathe
under water as a fish, throw fireballs as a blaze.

Morphing Time is for **Minecraft 26.1.2 on Fabric** and is **one single jar**, it only needs Fabric API.

## Install

1. Download `MorphingTime-FABRIC-26.1.2-x.y.z.jar` from [Releases](https://github.com/magicbanana666/MorphingTime/releases).
2. Put it in the `mods` folder together with [Fabric API](https://modrinth.com/mod/fabric-api) (Fabric Loader 0.19.5 or newer). Needed on the client and on the server.

## How to play

1. **Kill a mob** (or another player). It is unlocked and added to your collection (bosses like the Ender Dragon and the Wither need
   one kill too, other mobs can be configured to need more).
2. **Press `K`** to open the morph menu. It shows every mob you unlocked, with badges for its abilities and traits.
3. **Click a mob or player** to morph into it. As a player morph you get their skin and name. Click yourself (the player head at the top right) to become a player again.
4. **Press `R`** to quickly switch between your player shape and your last morph.
5. **Press `V`** to use the mob's ability (if it has one), EG shoot a fireball as a blaze or a ghast,
   teleport as an enderman, explode as a creeper. A cooldown shows at the bottom right.
6. **Press `J`** to choose a variant (colour of a sheep, type of a cat, ...). Scroll to cycle, press `J` again to confirm.

Holding `U` while looking at a mob unlocks it, if the server allows unlocking that way. All keys can be changed in Options > Controls.

### The morph menu

Every mob you unlocked is a card. Right-click a card to make it a favorite (favorites are listed first), and use
the search box to filter by name. The card you are morphed into has a green outline.

Along the bottom of each card are small badges for what the mob can do:

- **Gold**: an active ability you use with `V`
- **Green**: strengths, like flying, climbing walls or breathing under water
- **Red**: weaknesses, like burning in daylight or sinking in water
- **Blue**: other traits, like riding other mobs

Hover a badge to see what it does. The **Legend** button explains every badge, and **Traits: On/Off** hides them.

### What changes when you morph

- **Size and hitbox** of the mob: you can fit through small gaps as a chicken, or are huge as a ghast.
- **Health** of the mob, up to 40 (configurable): a chicken has 4, an iron golem 40. Your current health stays the same percentage.
- **Armor** like the mob (capped at 30).
- **All other stats** of the mob: attack damage (an iron golem hits for 15), knockback, knockback resistance,
  walking and swimming speed, jump strength, fall damage, gravity, burning time, breathing. Each stat and its
  limit can be changed in `morphing.json5` (`copiedStats`), and any attribute id works, also from other mods.
- **Traits**, passive abilities: flying, climbing, breathing under water, not taking fall damage, being
  immune to fire, night vision, fast swimming, slowness on land for fish, burning in sunlight for undead, ...
- **Mob behaviour**: hostile mobs ignore you while you are a hostile mob, and prey animals run from you
  when you are a wolf or a fox.
- Morph sounds and particles. Your armor and held items still show on the mob.

### Animations

- **Morphing**: your old model turns into the new one block by block, glowing white, in about a second.
  Other players see it too.
- **Unlocking**: when a kill unlocks a new mob, a glowing copy of it flies into you.

Both can be turned off in `morphing.json5` (`morphAnimation`, `unlockAnimation`); `morphAnimationTicks` sets how
long the morph takes.

### Commands (operators)

All commands are under `/morphingtime`, or the short `/morph`:

- `/morph addShape <players> <mob>`, `removeShape`, `clearShapes`, `hasShape` - manage unlocked morphs
- `/morph switchShape <players> <mob>` or `/morph switchShape <players> normal` - force a morph
- `/morph change2ndShape`, `show2ndShape`, `remove2ndShape` - the morph that `R` switches to
- `/morph addSkin <players> <player uuid or name>`, `removeSkin`, `clearSkins`, `hasSkin` - manage unlocked player morphs
- `/morph skin set <player> <uuid>`, `skin url <player> <slim> <url>`, `skin reset <player>`, `skin changeChatName <true/false>` - change a skin directly
- `/morph list-permissions`, `/morph skin list-permissions` and the blacklist commands - see the in-game suggestions

### Modded mobs

Every mob works, also the ones from other mods: you get their model, hitbox, health and stats automatically.
Mods that extend a vanilla mob (EG a new kind of zombie or spider) get its traits too. For other modded mobs
Morphing Time detects the basics on its own: flying (mobs that fly), climbing walls (mobs that climb like spiders),
breathing under water and fire immunity. Data packs can add more traits, variants and abilities
(`data/<namespace>/walkers/traits`, `.../variants`, `.../abilities`).

### Works with

- **Hats Reinvented**: your hat stays on while you are morphed.

## Configuration

The files are in `config/morphingtime/`:

- `unlocking.json5`: unlocking (kills needed per mob, creative unlocks everything, permissions, menu layout)
- `morphing.json5`: the morph itself (health/armor scaling, flight, abilities and their cooldowns, mob behaviour,
  blacklists)
- `skins.json5`: player morphs (EG whether your chat name changes with the skin)
- `core.json5`: internal settings

With `usePermissions` on (in `unlocking.json5`), a permission mod can use the nodes `morphingtime.morph`,
`morphingtime.type.minecraft.pig`, `morphingtime.command.addshape`, `morphingtime.skin.command.set`, ...
(see `/morph list-permissions` and `/morph skin list-permissions`). They are checked through the Fabric Permission API;
without a permission mod they are given to operators.

Unlocked morphs are yours forever by default. Set `killValue` (mobs) or `playerKillValue` (players) in
`unlocking.json5` to make a morph get used up after that many morphs per kill.

Traits, abilities and variants can be changed with data packs (`data/<namespace>/walkers/traits`, `.../variants`, `.../abilities`).

## Building

```
./gradlew build
```

Needs JDK 25. The jar ends up in `~/.gradle-builds/MorphingTime-Fabric/libs`. `./gradlew runClient -PextraMods=<jar>;<jar>` starts a dev client with other mods.

## License

[Don't Be an Asshole License](LICENSE.md): do whatever you want with it, port it, share it, just don't be an asshole.
