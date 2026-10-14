package com.mlc.mlcbot;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TotemInventoryTest {
    @Test void resurrectionLimitStopsEvenIfServerDoesNotConsumeItems() {
        Kit kit = new Kit(item(Material.TOTEM_OF_UNDYING, 1));
        kit.storage[35] = item(Material.TOTEM_OF_UNDYING, 1);
        TotemSupply supply = new TotemSupply(kit.inventory);
        assertEquals(2, supply.remaining());
        assertTrue(supply.consume()); // Successful resurrection, but the server kept the offhand item.
        supply.refill(kit.inventory);
        assertEquals(1, TotemInventory.count(kit.inventory));
        assertNull(kit.storage[35]);
        assertTrue(supply.consume());
        supply.refill(kit.inventory);
        assertEquals(0, TotemInventory.count(kit.inventory));
        assertFalse(supply.consume());
        assertEquals(0, supply.remaining());
    }

    @Test void nativeConsumptionIsNotCountedTwiceAndKitRefreshCannotResetTheLimit() {
        Kit kit = new Kit(item(Material.TOTEM_OF_UNDYING, 1));
        kit.storage[35] = item(Material.TOTEM_OF_UNDYING, 64);
        TotemSupply supply = new TotemSupply(kit.inventory);
        for (int remaining = 64; remaining >= 0; remaining--) {
            assertTrue(supply.consume());
            kit.offhand.set(null); // The native code consumes after the resurrection event.
            supply.refill(kit.inventory);
            assertEquals(remaining, TotemInventory.count(kit.inventory));
            assertEquals(remaining, supply.remaining());
        }
        kit.offhand.set(item(Material.TOTEM_OF_UNDYING, 1));
        kit.storage[35] = item(Material.TOTEM_OF_UNDYING, 64);
        supply.refill(kit.inventory);
        assertEquals(0, TotemInventory.count(kit.inventory));
        assertFalse(supply.consume());
    }

    @Test void emptyKitCannotGainResurrectionsFromLaterInventoryChanges() {
        Kit kit = new Kit(null);
        TotemSupply supply = new TotemSupply(kit.inventory);
        kit.storage[35] = item(Material.TOTEM_OF_UNDYING, 64);
        supply.refill(kit.inventory);
        assertEquals(0, TotemInventory.count(kit.inventory));
        assertFalse(supply.consume());
    }

    @Test void normalKitStopsAfterItsTwoActualTotemsAreConsumed() {
        Kit kit = new Kit(item(Material.TOTEM_OF_UNDYING, 1));
        kit.storage[35] = item(Material.TOTEM_OF_UNDYING, 1);
        assertEquals(2, TotemInventory.count(kit.inventory));
        kit.offhand.set(null); // Native resurrection consumes the first item.
        TotemInventory.refillOffhand(kit.inventory);
        assertNull(kit.storage[35]);
        assertEquals(1, TotemInventory.count(kit.inventory));
        kit.offhand.set(null);
        for (int tick = 0; tick < 100; tick++) TotemInventory.refillOffhand(kit.inventory);
        assertNull(kit.offhand.get());
        assertEquals(0, TotemInventory.count(kit.inventory));
    }

    @Test void cpvpKitMovesItsStackWithoutDuplicatingTheReserve() {
        Kit kit = new Kit(item(Material.TOTEM_OF_UNDYING, 1));
        kit.storage[35] = item(Material.TOTEM_OF_UNDYING, 64);
        assertEquals(65, TotemInventory.count(kit.inventory));
        for (int remaining = 64; remaining >= 0; remaining--) {
            kit.offhand.set(null);
            TotemInventory.refillOffhand(kit.inventory);
            assertEquals(remaining, TotemInventory.count(kit.inventory));
        }
        assertNull(kit.storage[35]);
        assertNull(kit.offhand.get());
    }

    @Test void refillKeepsAnOccupiedOffhandAndOtherKitItems() {
        ItemStack shield = item(Material.SHIELD, 1);
        Kit kit = new Kit(shield);
        kit.storage[4] = item(Material.TOTEM_OF_UNDYING, 2);
        kit.storage[5] = item(Material.GOLDEN_APPLE, 64);
        TotemInventory.refillOffhand(kit.inventory);
        assertSame(shield, kit.offhand.get());
        assertEquals(2, kit.storage[4].getAmount());
        assertEquals(2, TotemInventory.count(kit.inventory));
        kit.offhand.set(item(Material.AIR, 0));
        TotemInventory.refillOffhand(kit.inventory);
        assertEquals(2, TotemInventory.count(kit.inventory));
        assertEquals(64, kit.storage[5].getAmount());
    }

    private static ItemStack item(Material material, int count) {
        ItemStack item = mock(ItemStack.class);
        AtomicInteger amount = new AtomicInteger(count);
        when(item.getType()).thenReturn(material);
        when(item.getAmount()).thenAnswer(call -> amount.get());
        doAnswer(call -> { amount.set(call.getArgument(0)); return null; }).when(item).setAmount(anyInt());
        when(item.clone()).thenAnswer(call -> item(material, amount.get()));
        return item;
    }

    private static final class Kit {
        final PlayerInventory inventory = mock(PlayerInventory.class);
        final ItemStack[] storage = new ItemStack[36];
        final AtomicReference<ItemStack> offhand;
        Kit(ItemStack initialOffhand) {
            offhand = new AtomicReference<>(initialOffhand);
            when(inventory.getStorageContents()).thenReturn(storage);
            when(inventory.getItemInOffHand()).thenAnswer(call -> offhand.get());
            doAnswer(call -> { storage[call.getArgument(0)] = call.getArgument(1); return null; })
                    .when(inventory).setItem(anyInt(), nullable(ItemStack.class));
            doAnswer(call -> { offhand.set(call.getArgument(0)); return null; })
                    .when(inventory).setItemInOffHand(nullable(ItemStack.class));
        }
    }
}
