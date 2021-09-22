package se.fusion1013.plugin.nicobalt.particle;

import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public enum ParticleEffect {
    AMBIENT_ENTITY_EFFECT("SPELL_MOB_AMBIENT", Collections.singletonList("BEACON")),
    ANGRY_VILLAGER("VILLAGER_ANGRY", Collections.singletonList("IRON_DOOR")),
    ASH("ASH", Collections.singletonList("BLACKSTONE")),
    BARRIER("BARRIER", Collections.singletonList("BARRIER")),
    BLOCK("BLOCK_CRACK", Collections.singletonList("STONE")),
    BUBBLE("WATER_BUBBLE", Arrays.asList("BUBBLE_CORAL", "GLASS")),
    BUBBLE_COLUMN_UP("BUBBLE_COLUMN_UP", Collections.singletonList("MAGMA_BLOCK")),
    BUBBLE_POP("BUBBLE_POP", Collections.singletonList("BUBBLE_CORAL_FAN")),
    CAMPFIRE_COSY_SMOKE("CAMPFIRE_COSY_SMOKE", Collections.singletonList("CAMPFIRE")),
    CAMPFIRE_SIGNAL_SMOKE("CAMPFIRE_SIGNAL_SMOKE", Collections.singletonList("REDSTONE_TORCH")),
    CLOUD("CLOUD", Arrays.asList("WHITE_WOOL", "WOOL")),
    COMPOSTER("COMPOSTER", Collections.singletonList("COMPOSTER")),
    CRIMSON_SPORE("CRIMSON_SPORE", Collections.singletonList("CRIMSON_FUNGUS")),
    CRIT("CRIT", Collections.singletonList("IRON_SWORD")),
    CURRENT_DOWN("CURRENT_DOWN", Collections.singletonList("SOUL_SAND")),
    DAMAGE_INDICATOR("DAMAGE_INDICATOR", Collections.singletonList("BOW")),
    DOLPHIN("DOLPHIN", Collections.singletonList("DOLPHIN_SPAWN_EGG")),
    DRAGON_BREATH("DRAGON_BREATH", Arrays.asList("DRAGON_BREATH", "DRAGONS_BREATH")),
    DRIPPING_DRIPSTONE_LAVA("DRIPPING_DRIPSTONE_LAVA", Collections.singletonList("POINTED_DRIPSTONE")),
    DRIPPING_DRIPSTONE_WATER("DRIPPING_DRIPSTONE_WATER", Collections.singletonList("DRIPSTONE_BLOCK")),
    DRIPPING_HONEY("DRIPPING_HONEY", Collections.singletonList("BEE_NEST")),
    DRIPPING_LAVA("DRIP_LAVA", Collections.singletonList("LAVA_BUCKET")),
    DRIPPING_OBSIDIAN_TEAR("DRIPPING_OBSIDIAN_TEAR", Collections.singletonList("CRYING_OBSIDIAN")),
    DRIPPING_WATER("DRIP_WATER", Collections.singletonList("WATER_BUCKET")),
    DUST("REDSTONE", Collections.singletonList("REDSTONE")),
    DUST_COLOR_TRANSITION("DUST_COLOR_TRANSITION", Collections.singletonList("DEEPSLATE_REDSTONE_ORE")),
    ELDER_GUARDIAN("MOB_APPEARANCE", Arrays.asList("ELDER_GUARDIAN_SPAWN_EGG", "PRISMARINE_CRYSTALS")), // No thank you
    ELECTRIC_SPARK("ELECTRIC_SPARK", Collections.singletonList("LIGHTNING_ROD")),
    ENCHANT("ENCHANTMENT_TABLE", Arrays.asList("ENCHANTING_TABLE", "ENCHANTMENT_TABLE")),
    ENCHANTED_HIT("CRIT_MAGIC", Collections.singletonList("DIAMOND_SWORD")),
    END_ROD("END_ROD", Collections.singletonList("END_ROD")),
    ENTITY_EFFECT("SPELL_MOB", Collections.singletonList("GLOWSTONE_DUST")),
    EXPLOSION("EXPLOSION_LARGE", Arrays.asList("FIRE_CHARGE", "FIREBALL")),
    EXPLOSION_EMITTER("EXPLOSION_HUGE", Collections.singletonList("TNT")),
    FALLING_DRIPSTONE_LAVA("FALLING_DRIPSTONE_LAVA", Collections.singletonList("SMOOTH_BASALT")),
    FALLING_DRIPSTONE_WATER("FALLING_DRIPSTONE_WATER", Collections.singletonList("CALCITE")),
    FALLING_DUST("FALLING_DUST", Collections.singletonList("SAND")),
    FALLING_HONEY("FALLING_HONEY", Collections.singletonList("HONEY_BOTTLE")),
    FALLING_LAVA("FALLING_LAVA", Collections.singletonList("RED_DYE")),
    FALLING_NECTAR("FALLING_NECTAR", Collections.singletonList("HONEYCOMB")),
    FALLING_OBSIDIAN_TEAR("FALLING_OBSIDIAN_TEAR", Collections.singletonList("ANCIENT_DEBRIS")),
    FALLING_SPORE_BLOSSOM("FALLING_SPORE_BLOSSOM", Collections.singletonList("FLOWERING_AZALEA")),
    FALLING_WATER("FALLING_WATER", Collections.singletonList("BLUE_DYE")),
    FIREWORK("FIREWORKS_SPARK", Arrays.asList("FIREWORK_ROCKET", "FIREWORK")),
    FISHING("WATER_WAKE", Collections.singletonList("FISHING_ROD")),
    FLAME("FLAME", Collections.singletonList("BLAZE_POWDER")),
    FLASH("FLASH", Collections.singletonList("GOLD_INGOT")), // Also no thank you
    GLOW("GLOW", Collections.singletonList("GLOW_ITEM_FRAME")),
    GLOW_SQUID_INK("GLOW_SQUID_INK", Collections.singletonList("GLOW_INK_SAC")),
    FOOTSTEP("FOOTSTEP", Collections.singletonList("GRASS")), // Removed in Minecraft 1.13 :(
    HAPPY_VILLAGER("VILLAGER_HAPPY", Arrays.asList("DARK_OAK_DOOR_ITEM", "DARK_OAK_DOOR")),
    HEART("HEART", Arrays.asList("POPPY", "RED_ROSE")),
    INSTANT_EFFECT("SPELL_INSTANT", Arrays.asList("SPLASH_POTION", "POTION")),
    ITEM("ITEM_CRACK", Collections.singletonList("ITEM_FRAME")),
    ITEM_SLIME("SLIME", Collections.singletonList("SLIME_BALL")),
    ITEM_SNOWBALL("SNOWBALL", Arrays.asList("SNOWBALL", "SNOW_BALL")),
    LANDING_HONEY("LANDING_HONEY", Collections.singletonList("HONEY_BLOCK")),
    LANDING_LAVA("LANDING_LAVA", Collections.singletonList("ORANGE_DYE")),
    LANDING_OBSIDIAN_TEAR("LANDING_OBSIDIAN_TEAR", Collections.singletonList("NETHERITE_BLOCK")),
    LARGE_SMOKE("SMOKE_LARGE", Arrays.asList("COBWEB", "WEB")),
    LAVA("LAVA", Collections.singletonList("MAGMA_CREAM")),
    LIGHT("LIGHT", Collections.singletonList("LIGHT")),
    MYCELIUM("TOWN_AURA", Arrays.asList("MYCELIUM", "MYCEL")),
    NAUTILUS("NAUTILUS", Collections.singletonList("HEART_OF_THE_SEA")),
    NOTE("NOTE", Collections.singletonList("NOTE_BLOCK")),
    POOF("EXPLOSION_NORMAL", Arrays.asList("FIREWORK_STAR", "FIREWORK_CHARGE")), // The 1.13 combination of explode and showshovel
    PORTAL("PORTAL", Collections.singletonList("OBSIDIAN")),
    RAIN("WATER_DROP", Arrays.asList("PUFFERFISH_BUCKET", "LAPIS_BLOCK")),
    REVERSE_PORTAL("REVERSE_PORTAL", Collections.singletonList("FLINT_AND_STEEL")),
    SCRAPE("SCRAPE", Collections.singletonList("GOLDEN_AXE")),
    SMALL_FLAME("SMALL_FLAME", Collections.singletonList("CANDLE")),
    SMOKE("SMOKE_NORMAL", Collections.singletonList("TORCH")),
    SNEEZE("SNEEZE", Collections.singletonList("BAMBOO")),
    SNOWFLAKE("SNOWFLAKE", Collections.singletonList("POWDER_SNOW_BUCKET")),
    SOUL("SOUL", Collections.singletonList("SOUL_LANTERN")),
    SOUL_FIRE_FLAME("SOUL_FIRE_FLAME", Collections.singletonList("SOUL_CAMPFIRE")),
    SPELL("SPELL", Arrays.asList("POTION", "GLASS_BOTTLE")), // The Minecraft internal name for this is actually "effect", but that's the command name, so it's SPELL for the plugin instead
    SPIT("SPIT", Arrays.asList("LLAMA_SPAWN_EGG", "PUMPKIN_SEEDS")),
    SPLASH("WATER_SPLASH", Arrays.asList("SALMON", "FISH", "RAW_FISH")),
    SPORE_BLOSSOM_AIR("SPORE_BLOSSOM_AIR", Collections.singletonList("SPORE_BLOSSOM")),
    SQUID_INK("SQUID_INK", Collections.singletonList("INK_SAC")),
    SWEEP_ATTACK("SWEEP_ATTACK", Arrays.asList("GOLDEN_SWORD", "GOLD_SWORD")),
    TOTEM_OF_UNDYING("TOTEM", Arrays.asList("TOTEM_OF_UNDYING", "TOTEM")),
    UNDERWATER("SUSPENDED_DEPTH", Arrays.asList("TURTLE_HELMET", "SPONGE")),
    VIBRATION("VIBRATION", Collections.singletonList("SCULK_SENSOR")),
    WARPED_SPORE("WARPED_SPORE", Collections.singletonList("WARPED_FUNGUS")),
    WAX_OFF("WAX_OFF", Collections.singletonList("OXIDIZED_COPPER")),
    WAX_ON("WAX_ON", Collections.singletonList("WAXED_COPPER_BLOCK")),
    WHITE_ASH("WHITE_ASH", Collections.singletonList("BASALT")),
    WITCH("SPELL_WITCH", Collections.singletonList("CAULDRON"));

    private Particle internalEnum;
    private String effectName;
    private boolean supported = true;
    private boolean enabled = true;

    ParticleEffect(String enumName, List<String> defaultIconMaterialNames){
        effectName = enumName;

        this.internalEnum = Stream.of(Particle.values()).filter(x -> x.name().equals(enumName)).findFirst().orElse(null);
    }

    public static List<ParticleEffect> getEnabledEffects(){
        List<ParticleEffect> effects = new ArrayList<>();
        for (ParticleEffect pe : values()){
            effects.add(pe);
        }
        return effects;
    }

    public static List<String> getEnabledEffectNames(){
        List<String> effectNames = new ArrayList<>();
        for (ParticleEffect pe : getEnabledEffects()){
            effectNames.add(pe.getName().toLowerCase());
        }
        return effectNames;
    }

    public String getName(){
        return this.effectName;
    }

    public boolean isSupported(){
        return this.supported;
    }

    public boolean isEnabled(){
        return this.enabled;
    }

    public static ParticleEffect fromName(String name){
        return Stream.of(values()).filter(x -> x.isSupported() && x.isEnabled() & x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Particle getSpigotEnum(){
        return this.internalEnum;
    }
}
