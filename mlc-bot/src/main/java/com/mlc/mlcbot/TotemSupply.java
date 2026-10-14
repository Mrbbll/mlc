package com.mlc.mlcbot;

import org.bukkit.inventory.PlayerInventory;

/** A lifetime limit captured from the equipped kit, independent of server item-consumption settings. */
public final class TotemSupply {
    private int remaining;

    public TotemSupply(PlayerInventory inventory) {
        remaining = TotemInventory.count(inventory);
    }

    public int remaining() { return remaining; }

    public boolean consume() {
        if (remaining == 0) return false;
        --remaining;
        return true;
    }

    public void refill(PlayerInventory inventory) {
        // Native resurrection normally consumes the item after the event returns.
        // Remove any surplus if a server setting or another listener kept/recreated it.
        TotemInventory.limit(inventory, remaining);
        TotemInventory.refillOffhand(inventory);
    }
}
