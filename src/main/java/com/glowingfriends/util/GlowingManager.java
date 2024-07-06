package com.glowingfriends.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GlowingManager {
    private static final Set<UUID> glowingPlayers = new HashSet<>();
    private static final Gson gson = new Gson();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "GlowingFriends.json");

    static {
        loadGlowingPlayers();
    }

    public static void addGlowingPlayer(UUID playerUUID) {
        glowingPlayers.add(playerUUID);
        saveGlowingPlayers();
    }

    public static void removeGlowingPlayer(UUID playerUUID) {
        glowingPlayers.remove(playerUUID);
        saveGlowingPlayers();
    }

    public static boolean isPlayerGlowing(UUID playerUUID) {
        boolean result = glowingPlayers.contains(playerUUID);
        return result;
    }

    public static void loadGlowingPlayers() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                Type type = new TypeToken<Set<UUID>>() {}.getType();
                Set<UUID> loadedPlayers = gson.fromJson(reader, type);
                if (loadedPlayers != null) {
                    glowingPlayers.addAll(loadedPlayers);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveGlowingPlayers() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(glowingPlayers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
