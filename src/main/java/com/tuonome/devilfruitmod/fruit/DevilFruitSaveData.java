package com.tuonome.devilfruitmod.fruit;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.*;

public class DevilFruitSaveData extends SavedData {

    private static final String DATA_NAME = "devilfruit_data";
    private final Map<UUID, String> playerFruit = new HashMap<>();
    private final Map<UUID, Boolean> logiaActive = new HashMap<>();

    public static DevilFruitSaveData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            DevilFruitSaveData::load, DevilFruitSaveData::new, DATA_NAME
        );
    }

    private static DevilFruitSaveData load(CompoundTag tag) {
        DevilFruitSaveData data = new DevilFruitSaveData();
        CompoundTag players = tag.getCompound("players");
        for (String key : players.getAllKeys()) {
            CompoundTag p = players.getCompound(key);
            UUID uuid = UUID.fromString(key);
            if (p.contains("fruit")) data.playerFruit.put(uuid, p.getString("fruit"));
            data.logiaActive.put(uuid, p.getBoolean("logia"));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag players = new CompoundTag();
        playerFruit.forEach((uuid, fruit) -> {
            CompoundTag p = new CompoundTag();
            p.putString("fruit", fruit);
            p.putBoolean("logia", logiaActive.getOrDefault(uuid, false));
            players.put(uuid.toString(), p);
        });
        tag.put("players", players);
        return tag;
    }

    public boolean hasFruit(UUID uuid) {
        return playerFruit.containsKey(uuid);
    }

    public DevilFruit getFruit(UUID uuid) {
        String name = playerFruit.get(uuid);
        if (name == null) return null;
        try { return DevilFruit.valueOf(name); }
        catch (Exception e) { return null; }
    }

    public void setFruit(UUID uuid, DevilFruit fruit) {
        playerFruit.put(uuid, fruit.name());
        setDirty();
    }

    public void removeFruit(UUID uuid) {
        playerFruit.remove(uuid);
        logiaActive.remove(uuid);
        setDirty();
    }

    public boolean isLogiaActive(UUID uuid) {
        return logiaActive.getOrDefault(uuid, false);
    }

    public void setLogiaActive(UUID uuid, boolean active) {
        logiaActive.put(uuid, active);
        setDirty();
    }
}
