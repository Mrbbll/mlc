package com.mlc.mlc.mlcmain.menu;

import com.mlc.mlcstyte.MlcStyte;
import com.mlc.mlcstyte.StyteFormatter;
import com.mlc.mlcstyte.chatmanager.ChatEffect;
import com.mlc.mlcstyte.chatmanager.formattermanager.Selection;
import com.mlc.mlcstyte.chatmanager.formattermanager.Target;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.mlc.mlc.Mlc.instance;

public final class chatformatmenu implements Listener {
    private static final int SIZE = 54;
    private static final int NAME_TAB = 2;
    private static final int PREVIEW = 4;
    private static final int MESSAGE_TAB = 6;
    private static final int FIRST_EFFECT = 9;
    private static final int BACK = 47;
    private static final int PACK_INFO = 51;

    private static final TextColor MINT = TextColor.color(0xB8FFDE);
    private static final TextColor LILAC = TextColor.color(0xD8B9FF);
    private static final TextColor LIGHT = TextColor.color(0xF2ECFF);
    private static final TextColor MUTED = TextColor.color(0xA9A5BA);
    private static final TextColor GOLD = TextColor.color(0xFFDA8F);
    private static final Component SAMPLE = line("Hello!  聊天特效预览", LIGHT);

    private static final class Holder implements InventoryHolder {
        private Inventory inventory;
        private Target target = Target.NAME;

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }
    }

    public static void open(Player player) {
        Holder holder = new Holder();
        holder.inventory = Bukkit.createInventory(holder, SIZE, line("聊天特效", LILAC));
        render(player, holder);
        player.openInventory(holder.inventory);
    }

    private static void render(Player player, Holder holder) {
        Inventory inventory = holder.inventory;
        Selection selection = MlcStyte.chatEffects().selection(player.getUniqueId());
        ChatEffect active = selection.effect(holder.target);
        TextColor accent = holder.target == Target.NAME ? MINT : LILAC;

        ItemStack border = item(Material.BLACK_STAINED_GLASS_PANE, Component.text(" "), false);
        ItemStack corner = item(holder.target == Target.NAME
                ? Material.CYAN_STAINED_GLASS_PANE : Material.PURPLE_STAINED_GLASS_PANE,
                Component.text(" "), false);
        for (int column = 0; column < 9; column++) {
            ItemStack frame = column == 0 || column == 8 ? corner : border;
            inventory.setItem(column, frame);
            inventory.setItem(SIZE - 9 + column, frame);
        }

        inventory.setItem(NAME_TAB, tab(Material.NAME_TAG, "名字", MINT, holder.target == Target.NAME, selection.name()));
        inventory.setItem(MESSAGE_TAB, tab(Material.FEATHER, "聊天正文", LILAC, holder.target == Target.MESSAGE, selection.message()));
        inventory.setItem(PREVIEW, item(Material.WRITTEN_BOOK, line("聊天预览", GOLD), false,
                selection.name().apply(line(player.getName(), LIGHT))
                        .append(line("  »  ", GOLD))
                        .append(selection.message().apply(SAMPLE)),
                line("点击发送一条仅自己可见的预览", MUTED)));
        inventory.setItem(BACK, item(Material.ARROW, line("返回快捷菜单", MINT), false));
        inventory.setItem(PACK_INFO, item(Material.SPYGLASS, line("资源包", LIGHT), false,
                line("启用 MLC 资源包后即可看到动态特效", MUTED)));

        inventory.setItem(FIRST_EFFECT, effectItem(ChatEffect.DEFAULT, active, accent));
        List<ChatEffect> presets = ChatEffect.presets();
        for (int index = 0; index < presets.size(); index++) {
            inventory.setItem(FIRST_EFFECT + 1 + index, effectItem(presets.get(index), active, accent));
        }
    }

    private static ItemStack tab(Material icon, String label, TextColor accent, boolean active, ChatEffect effect) {
        return item(icon, line(label, accent), active,
                line("当前特效  ", MUTED).append(line(effect.label(), LIGHT)),
                line(active ? "正在编辑" : "点击切换", active ? accent : MUTED));
    }

    private static ItemStack effectItem(ChatEffect effect, ChatEffect active, TextColor accent) {
        boolean selected = effect == active;
        return item(effect.icon(), line(effect.label(), selected ? accent : LIGHT), selected,
                effect.apply(SAMPLE),
                line(selected ? "已选中" : "点击应用", selected ? accent : MUTED));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder(false) instanceof Holder holder)) return;
        event.setCancelled(true);
        if (event.getClick() != ClickType.LEFT || !(event.getWhoClicked() instanceof Player player)) return;

        int slot = event.getRawSlot();
        switch (slot) {
            case NAME_TAB, MESSAGE_TAB -> {
                holder.target = slot == NAME_TAB ? Target.NAME : Target.MESSAGE;
                render(player, holder);
            }
            case PREVIEW, BACK -> Bukkit.getScheduler().runTask(instance, () -> {
                if (!player.isOnline() || !player.getOpenInventory().getTopInventory().equals(inventory)) return;
                if (slot == BACK) {
                    Mlcmenu.open(player);
                } else {
                    player.closeInventory();
                    sendPreview(player);
                }
            });
            default -> {
                int effectIndex = slot - FIRST_EFFECT - 1;
                List<ChatEffect> presets = ChatEffect.presets();
                if (slot == FIRST_EFFECT || (effectIndex >= 0 && effectIndex < presets.size())) {
                    ChatEffect effect = slot == FIRST_EFFECT ? ChatEffect.DEFAULT : presets.get(effectIndex);
                    MlcStyte.chatEffects().select(player, holder.target, effect);
                    render(player, holder);
                }
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (top.getHolder(false) instanceof Holder && event.getRawSlots().stream().anyMatch(slot -> slot < SIZE)) {
            event.setCancelled(true);
        }
    }

    private static void sendPreview(Player player) {
        Selection selection = MlcStyte.chatEffects().selection(player.getUniqueId());
        Component name = Component.text(player.getName());
        player.sendMessage(StyteFormatter.buildFormat(player, Component.empty(), name, Component.empty(),
                selection.message().apply(SAMPLE), selection.name().apply(player.displayName())));
    }

    private static Component line(String text, TextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }

    private static ItemStack item(Material material, Component name, boolean highlighted, Component... lore) {
        ItemStack item = ItemStack.of(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        if (lore.length > 0) meta.lore(List.of(lore));
        if (highlighted) meta.setEnchantmentGlintOverride(true);
        item.setItemMeta(meta);
        return item;
    }
}
