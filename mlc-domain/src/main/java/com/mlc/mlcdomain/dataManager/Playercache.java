package com.mlc.mlcdomain.dataManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Playercache {
    public static Map<UUID, PlayerData> playercache = new HashMap<>();

    public static void clearCache(){
        playercache.clear();
    }

    public static void addCache(UUID playerUuid,PlayerData playerData){
        playercache.put(playerUuid,playerData);
    }
    public static PlayerData getCache(UUID playerUuid){
        return playercache.get(playerUuid);
    }
    public static boolean hasCache(UUID playerUuid){
        return playercache.containsKey(playerUuid);
    }
    public static void removeCache(UUID playerUuid){
        playercache.remove(playerUuid);
    }
    public static void updateCache(UUID playerUuid,PlayerData playerData){
        playercache.put(playerUuid,playerData);
    }

}
