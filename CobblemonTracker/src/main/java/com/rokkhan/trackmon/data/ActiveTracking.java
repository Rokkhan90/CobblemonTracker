package com.rokkhan.trackmon.data;

import java.util.*;

/**
 * Stores which Pokémon are actively being tracked for chat notifications.
 * This is separate from TrackingStorage, which stores the persistent list.
 */
public class ActiveTracking {

    public static Set<String> getActive(UUID player) {
        return active.getOrDefault(player, Set.of());
    }

    // In-memory map of player UUIDs to actively tracked Pokémon names
    private static final Map<UUID, Set<String>> active = new HashMap<>();

    /**
     * Sets the list of actively tracked Pokémon for a player.
     * This replaces any previously active list.
     */
    public static void setActive(UUID player, List<String> names) {
        Set<String> lower = new HashSet<>(names.stream().map(String::toLowerCase).toList());
        active.put(player, lower);
        System.out.println("ActiveTracking.setActive: " + player + " → " + lower);
    }

    /**
     * Removes specific Pokémon from the active tracking list for a player.
     */
    public static void remove(UUID player, List<String> names) {
        active.computeIfPresent(player, (uuid, set) -> {
            set.removeAll(names.stream().map(String::toLowerCase).toList());
            return set;
        });
    }

    /**
     * Clears all active tracking for a player.
     */
    public static void clear(UUID player) {
        active.remove(player);
    }

    /**
     * Checks if a specific Pokémon is actively tracked by the player.
     */
    public static boolean isActive(UUID player, String name) {
        boolean result = active.getOrDefault(player, Set.of()).contains(name.toLowerCase());
        System.out.println("ActiveTracking.isActive: " + player + " → " + name + " = " + result);
        return result;
    }
}
