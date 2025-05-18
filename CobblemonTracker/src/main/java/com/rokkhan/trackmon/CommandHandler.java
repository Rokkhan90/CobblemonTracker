package com.rokkhan.trackmon;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import com.rokkhan.trackmon.data.TrackingStorage;
import com.rokkhan.trackmon.data.ActiveTracking;

import java.util.*;

/**
 * Handles the /trackmon command and its subcommands.
 */
public class CommandHandler {

    // Stores tracked Pokémon per player
    private static final Map<UUID, Set<String>> trackedPokemon = new HashMap<>();

    /**
     * Registers the /trackmon command with subcommands and autocomplete.
     */
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("trackmon")
                // /trackmon track <pokemon>
                .then(Commands.literal("track")
                        .then(Commands.argument("pokemon", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    for (String name : PokemonSpecies.INSTANCE.getSpecies().stream().map(s -> s.getName().toLowerCase()).toList()) {
                                        builder.suggest(name);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "pokemon").toLowerCase();
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    trackedPokemon.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(name);
                                    context.getSource().sendSuccess(() -> Component.literal("Tracking " + name), false);
                                    return 1;
                                })))

                // /trackmon stop <pokemon>
                .then(Commands.literal("stop")
                        .then(Commands.argument("pokemon", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    for (String name : PokemonSpecies.INSTANCE.getSpecies().stream().map(s -> s.getName().toLowerCase()).toList()) {
                                        builder.suggest(name);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "pokemon").toLowerCase();
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    UUID uuid = player.getUUID();

                                    // Entferne das Pokémon nur aus der aktiven Liste
                                    ActiveTracking.remove(uuid, List.of(name));

                                    context.getSource().sendSuccess(() -> Component.literal("Stopped tracking " + name), false);
                                    return 1;
                                })))


                // /trackmon clear
                .then(Commands.literal("clear")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            UUID uuid = player.getUUID();

                            TrackingStorage.clear(uuid);
                            ActiveTracking.clear(uuid);

                            context.getSource().sendSuccess(() -> Component.literal("Cleared all tracked Pokémon."), false);
                            return 1;
                        })
                )

                // /trackmon list
                .then(Commands.literal("list")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            List<String> tracked = TrackingStorage.getTracked(player.getUUID());

                            if (tracked.isEmpty()) {
                                context.getSource().sendSuccess(() -> Component.literal("You are not tracking any Pokémon."), false);
                            } else {
                                context.getSource().sendSuccess(() -> Component.literal("Tracking: " + String.join(", ", tracked)), false);
                            }
                            return 1;
                        })
                )

                // /trackmon starter
                .then(Commands.literal("starter")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            UUID uuid = player.getUUID();

                            List<String> current = new ArrayList<>(TrackingStorage.getTracked(uuid));
                            for (String starter : Config.starterPokemon) {
                                if (!current.contains(starter)) {
                                    current.add(starter);
                                }
                            }
                            TrackingStorage.setTracked(uuid, current);
                            ActiveTracking.setActive(uuid, Config.starterPokemon);

                            context.getSource().sendSuccess(() -> Component.literal("Tracking all starter Pokémon."), false);
                            return 1;
                        })));
    }

    /**
     * Checks if a Pokémon is currently tracked by the player.
     */
    /*public static boolean isTracked(ServerPlayer player, String pokemonName) {
        return trackedPokemon.getOrDefault(player.getUUID(), Set.of()).contains(pokemonName.toLowerCase());
        }
        */
}
