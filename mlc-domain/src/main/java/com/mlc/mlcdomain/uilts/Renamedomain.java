package com.mlc.mlcdomain.uilts;



import com.mlc.mlcdomain.dataManager.Databasemanager;
import com.mlc.mlcdomain.dataManager.DomainData;
import org.bukkit.entity.Player;

import static com.mlc.mlcdomain.Mlcdomain.instance;

public class Renamedomain {
    public Renamedomain(){

    }
    public static void renameDomain(Player player, DomainData domainData, String newDomainName){
        if (Databasemanager.updateDomain(domainData, newDomainName)) {
            player.sendMessage("领地重命名成功！");
        } else {
            player.sendMessage("领地重命名失败！");
        }
    }
}
