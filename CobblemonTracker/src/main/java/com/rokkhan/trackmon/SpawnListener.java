package com.rokkhan.trackmon;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.rokkhan.trackmon.data.ActiveTracking;
import com.rokkhan.trackmon.data.TrackingStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.ChatFormatting;

/**
 * Listens for Pokémon spawns in the world and notifies players
 * if a tracked Pokémon appears nearby and is actively being tracked.
 */
public class SpawnListener {

    /**
     * Called whenever an entity joins the world (spawns).
     */
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        Level level = event.getLevel();


        if (level.isClientSide()) return;

        Entity entity = event.getEntity();

        if (entity instanceof PokemonEntity pokemonEntity) {
            if (pokemonEntity.getOwner() != null) return;

            String rawName = pokemonEntity.getPokemon().getSpecies().getName();
            String name = rawName.toLowerCase(); // für Vergleich
            String displayName = rawName.substring(0, 1).toUpperCase() + rawName.substring(1); // für Anzeige

            BlockPos pos = entity.blockPosition();

            for (Player player : level.players()) {
                if (player instanceof ServerPlayer serverPlayer) {
                    var uuid = serverPlayer.getUUID();
                    double distanceSquared = serverPlayer.distanceToSqr(pokemonEntity);

                    if (distanceSquared <= Config.trackingRadius * Config.trackingRadius) {
                        if (TrackingStorage.isTracked(uuid, name) && ActiveTracking.isActive(uuid, name)) {
                            serverPlayer.sendSystemMessage(
                                    Component.literal("")
                                            .append(Component.literal(displayName).withStyle(ChatFormatting.RED))
                                            .append(Component.literal(" has spawned at: " + pos.toShortString()).withStyle(Style.EMPTY))
                            );

                            if (Config.enableTrackingLog) {
                                System.out.println("Tracked spawn: " + displayName + " at " + pos);
                            }
                        }
                    }
                }
            }
        }
    }

}
