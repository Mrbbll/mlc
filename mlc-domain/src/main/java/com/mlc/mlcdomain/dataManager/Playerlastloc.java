package com.mlc.mlcdomain.dataManager;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Playerlastloc {
    public static Map<UUID, Location> playerlastloc = new HashMap<>();

    public Playerlastloc() {
    }

    public Playerlastloc(Map<UUID, Location> playerlastloc) {
        this.playerlastloc = playerlastloc;
    }




    /**
     * 获取
     * @return playerlastloc
     */
    public static Map<UUID, Location> getPlayerlastloc() {
        return playerlastloc;
    }

    /**
     * 设置
     * @param playerlastloc
     */
    public static void setPlayerlastloc(Map<UUID, Location> playerlastloc) {
        Playerlastloc.playerlastloc = playerlastloc;
    }

    public String toString() {
        return "Playerlastloc{playerlastloc = " + playerlastloc + "}";
    }
}
