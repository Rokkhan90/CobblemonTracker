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

    private static final Gson GSON = new Gson();
    private static final Type TYPE = new TypeToken<Map<UUID, List<String>>>() {}.getType();
    private static final File FILE = new File("config/trackmon/tracked_pokemon.json");

    private static Map<UUID, List<String>> data = new HashMap<>();

    static {
        load();
    }

    public static void load() {
        try {
            if (FILE.exists()) {
                try (FileReader reader = new FileReader(FILE)) {
                    data = GSON.fromJson(reader, TYPE);
                }
            } else {
                FILE.getParentFile().mkdirs();
                save(); // Erstellt leere Datei
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(data, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getTracked(UUID playerUUID) {
        return data.getOrDefault(playerUUID, new ArrayList<>());
    }

    public static void setTracked(UUID playerUUID, List<String> tracked) {
        data.put(playerUUID, new ArrayList<>(tracked));
        save();
    }

    public static void clear(UUID playerUUID) {
        data.remove(playerUUID);
        save();
    }

    public static boolean isTracked(UUID playerUUID, String name) {
        return getTracked(playerUUID).contains(name.toLowerCase());
    }
}
