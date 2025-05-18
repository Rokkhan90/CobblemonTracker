package com.rokkhan.trackmon.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;

public class TrackingStorage {

    // Gson instance for JSON serialization/deserialization
    private static final Gson GSON = new Gson();

    // Type token for deserializing the map of UUIDs to tracked Pokémon lists
    private static final Type TYPE = new TypeToken<Map<UUID, List<String>>>() {}.getType();

    // File location for storing tracked Pokémon data
    private static final File FILE = new File("config/trackmon/tracked_pokemon.json");

    // In-memory storage of tracked Pokémon per player UUID
    private static Map<UUID, List<String>> data = new HashMap<>();

    // Static initializer to load data when the class is first accessed
    static {
        load();
    }

    /**
     * Loads tracked Pokémon data from the JSON file.
     * If the file does not exist, it creates the necessary directories and an empty file.
     */
    public static void load() {
        try {
            if (FILE.exists()) {
                try (FileReader reader = new FileReader(FILE)) {
                    data = GSON.fromJson(reader, TYPE);
                }
            } else {
                FILE.getParentFile().mkdirs();
                save(); // Create empty file
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current tracked Pokémon data to the JSON file.
     */
    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(data, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the list of tracked Pokémon for a given player UUID.
     *
     * @param playerUUID The UUID of the player.
     * @return A list of Pokémon names being tracked by the player.
     */
    public static List<String> getTracked(UUID playerUUID) {
        return data.getOrDefault(playerUUID, new ArrayList<>());
    }

    /**
     * Sets the list of tracked Pokémon for a given player UUID and saves the data.
     *
     * @param playerUUID The UUID of the player.
     * @param tracked    The list of Pokémon names to track.
     */
    public static void setTracked(UUID playerUUID, List<String> tracked) {
        data.put(playerUUID, new ArrayList<>(tracked));
        save();
    }

    /**
     * Clears the tracked Pokémon list for a given player UUID and saves the data.
     *
     * @param playerUUID The UUID of the player.
     */
    public static void clear(UUID playerUUID) {
        data.remove(playerUUID);
        save();
    }

    /**
     * Checks if a specific Pokémon is being tracked by a player.
     *
     * @param playerUUID The UUID of the player.
     * @param name       The name of the Pokémon.
     * @return True if the Pokémon is being tracked, false otherwise.
     */
    public static boolean isTracked(UUID playerUUID, String name) {
        return getTracked(playerUUID).contains(name.toLowerCase());
    }
}
