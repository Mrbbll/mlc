package com.mlc.mlc.mlcmain.anvilcost;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.view.AnvilView;

public class Anvilcost implements Listener {
    public final int MAX_COST = 10000;
    @EventHandler
    public void onopenanvil(InventoryOpenEvent event){
        if (!(event.getView() instanceof AnvilView anvil)) {
        return;
    }

        anvil.setMaximumRepairCost(MAX_COST);
        anvil.bypassEnchantmentLevelRestriction(false);
    }
}
