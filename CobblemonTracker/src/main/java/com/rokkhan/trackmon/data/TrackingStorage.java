package com.rokkhan.trackmon.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
                save(); // Create empty file
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
        // Alle Namen in Kleinbuchstaben speichern
        List<String> lowerCaseList = tracked.stream()
                .map(String::toLowerCase)
                .toList();

        System.out.println("TrackingStorage.setTracked: " + playerUUID + " → " + lowerCaseList);
        data.put(playerUUID, new ArrayList<>(lowerCaseList));
        save();
    }

    public static void clear(UUID playerUUID) {
        data.remove(playerUUID);
        save();
    }

    public static boolean isTracked(UUID playerUUID, String name) {
        boolean result = getTracked(playerUUID).contains(name.toLowerCase());
        System.out.println("TrackingStorage.isTracked: " + playerUUID + " ? " + name.toLowerCase() + " = " + result);
        return result;
    }
}
