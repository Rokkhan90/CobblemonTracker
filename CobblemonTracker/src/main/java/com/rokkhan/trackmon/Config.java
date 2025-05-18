package com.rokkhan.trackmon;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

/**
 * Configuration class for the Trackmon mod.
 * Defines and loads configurable settings from the config file.
 */
@EventBusSubscriber(modid = Trackmon.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    // Builder for defining config values
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    /**
     * Radius (in blocks) around the player to detect tracked Pokémon spawns.
     * Default: 100, Min: 1, Max: 1000
     */
    public static final ModConfigSpec.IntValue TRACKING_RADIUS = BUILDER
            .comment("Radius in blocks for tracking Pokémon spawns")
            .defineInRange("trackingRadius", 100, 1, 1000);

    /**
     * List of starter Pokémon names to track when using /trackmon starter.
     */
    public static final ModConfigSpec.ConfigValue<List<? extends String>> STARTER_POKEMON = BUILDER
            .comment("List of starter Pokémon names")
            .defineListAllowEmpty("starterPokemon",
                    List.of(
                            // Gen 1 – Kanto
                            "bulbasaur", "charmander", "squirtle", "pikachu",

                            // Gen 2 – Johto
                            "chikorita", "cyndaquil", "totodile",

                            // Gen 3 – Hoenn
                            "treecko", "torchic", "mudkip",

                            // Gen 4 – Sinnoh
                            "turtwig", "chimchar", "piplup",

                            // Gen 5 – Einall / Unova
                            "snivy", "tepig", "oshawott",

                            // Gen 6 – Kalos
                            "chespin", "fennekin", "froakie",

                            // Gen 7 – Alola
                            "rowlet", "litten", "popplio",

                            // Gen 8 – Galar
                            "grookey", "scorbunny", "sobble",

                            // Gen 9 – Paldea
                            "sprigatito", "fuecoco", "quaxly",

                            // Hisui-Starter
                            "cyndaquil_hisui", "oshawott_hisui", "rowlet_hisui"
                    )
                    ,
                    obj -> obj instanceof String);

    /**
     * Whether to log tracked Pokémon spawns to the console.
     */
    public static final ModConfigSpec.BooleanValue ENABLE_TRACKING_LOG = BUILDER
            .comment("Log tracked Pokémon spawns to console")
            .define("enableTrackingLog", true);

    /**
     * List of Pokémon names to exclude from the GUI list.
     */
    public static final ModConfigSpec.ConfigValue<List<? extends String>> EXCLUDED_POKEMON = BUILDER
            .comment("List of Pokémon names to exclude from the GUI")
            .defineListAllowEmpty("excludedPokemon", List.of(), obj -> obj instanceof String);

    // Final config spec used by NeoForge
    public static final ModConfigSpec SPEC = BUILDER.build();

    // Runtime values (populated when config is loaded)
    public static int trackingRadius;
    public static List<String> starterPokemon;
    public static boolean enableTrackingLog;
    public static List<String> excludedPokemon;

    /**
     * Called when the config is loaded or reloaded.
     * Copies values from the config spec into runtime variables.
     */
    @net.neoforged.bus.api.SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        trackingRadius = TRACKING_RADIUS.get();
        starterPokemon = STARTER_POKEMON.get().stream().map(String::toLowerCase).toList();
        enableTrackingLog = ENABLE_TRACKING_LOG.get();
        excludedPokemon = EXCLUDED_POKEMON.get().stream().map(String::toLowerCase).toList();
    }
}
