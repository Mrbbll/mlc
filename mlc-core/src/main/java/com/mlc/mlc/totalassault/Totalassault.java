package com.mlc.mlc.totalassault;

import io.papermc.paper.persistence.PersistentDataKey;
import io.papermc.paper.persistence.PersistentDataViewHolder;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

public class Totalassault {
    public static void start(Player player){
        ;
        PersistentDataHolder persistentDataHolder = player.getPersistentDataContainer().getOrDefault(PersistentDataKey.of(Key.key("mlc"), PersistentDataType<String,>),);
    }

}
