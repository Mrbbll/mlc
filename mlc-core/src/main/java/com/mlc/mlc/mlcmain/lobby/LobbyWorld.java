package com.mlc.mlc.mlcmain.lobby;

import io.papermc.paper.math.Position;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.Objects;

public class LobbyWorld {

    public static WorldCreator wc =
            new WorldCreator( Objects.requireNonNull(NamespacedKey.fromString("mlc:lobbyworld")))
                    .type(WorldType.FLAT)
                    .bonusChest(false);
    public static World lobbyworld;

    public static void init(){
        wc.type(WorldType.FLAT);

        lobbyworld = Bukkit.createWorld(wc);

        if(lobbyworld!=null){
            lobbyworld.setTime(6000);
            lobbyworld.setDifficulty(Difficulty.PEACEFUL);
            lobbyworld.setGameRule(GameRules.KEEP_INVENTORY,true);
            lobbyworld.setGameRule(GameRules.MOB_GRIEFING,false);
            lobbyworld.setGameRule(GameRules.ELYTRA_MOVEMENT_CHECK,false);
            lobbyworld.setGameRule(GameRules.PLAYER_MOVEMENT_CHECK,false);
            lobbyworld.setGameRule(GameRules.SPAWN_MOBS,false);
            lobbyworld.setGameRule(GameRules.ADVANCE_TIME,false);
            lobbyworld.setGameRule(GameRules.ADVANCE_WEATHER,false);
            lobbyworld.setGameRule(GameRules.IMMEDIATE_RESPAWN,true);
            lobbyworld.setGameRule(GameRules.RESPAWN_RADIUS,0);
            lobbyworld.setGameRule(GameRules.SPAWN_MONSTERS,false);
            lobbyworld.setGameRule(GameRules.RANDOM_TICK_SPEED,0);
            lobbyworld.setHardcore(false);
        }

    }

    public static void teleporttolobbyworld(Player player){
        player.teleport(lobbyworld.getSpawnLocation());

    }
}
