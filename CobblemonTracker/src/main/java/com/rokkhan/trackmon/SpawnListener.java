package com.rokkhan.trackmon;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;

/**
 * Listens for Pokémon spawns in the world and notifies players
 * if a tracked Pokémon appears nearby.
 */
public class SpawnListener {

    /**
     * Called whenever an entity joins the world (spawns).
     */
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        Level level = event.getLevel();

        // Ignore client-side events
        if (level.isClientSide()) return;

        Entity entity = event.getEntity();

        // Only handle Pokémon entities
        if (entity instanceof PokemonEntity pokemonEntity) {

            // Ignore Pokémon that were sent out by a player (e.g. from a Pokéball)
            if (pokemonEntity.getOwner() != null) return;

            // Get the Pokémon's species name and format it (capitalize first letter)
            String rawName = pokemonEntity.getPokemon().getSpecies().getName();
            String name = rawName.substring(0, 1).toUpperCase() + rawName.substring(1).toLowerCase();

            // Get the spawn position
            BlockPos pos = entity.blockPosition();

            // Check all players in the world
            for (Player player : level.players()) {
                if (player instanceof ServerPlayer serverPlayer) {

                    // Check if the Pokémon is within tracking radius
                    double distanceSquared = serverPlayer.distanceToSqr(pokemonEntity);
                    if (distanceSquared <= Config.trackingRadius * Config.trackingRadius) {

                        // Check if the player is tracking this Pokémon
                        if (CommandHandler.isTracked(serverPlayer, name)) {

                            // Send a system message to the player
                            serverPlayer.sendSystemMessage(
                                    Component.literal(name)
                                            .withStyle(Style.EMPTY.withColor(ChatFormatting.RED))
                                            .append(Component.literal(" has spawned at: " + pos.toShortString()))
                            );

                            // Optional: log the spawn to the console
                            if (Config.enableTrackingLog) {
                                System.out.println("Tracked spawn: " + name + " at " + pos);
                            }
                        }
                    }
                }
            }
        }
    }
}
