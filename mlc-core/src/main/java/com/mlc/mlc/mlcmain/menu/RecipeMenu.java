package com.mlc.mlc.mlcmain.menu;

import net.kyori.adventure.text.Component;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.miniMessage;

/** 查询服务器注册的配方；界面中的物品均为不可取出的展示物品。 */
public final class RecipeMenu implements Listener {
    private static final int[] GRID = {10, 11, 12, 19, 20, 21, 28, 29, 30};
    private static final int PREVIOUS = 37, INFO = 40, NEXT = 43;

    private static final class Holder implements InventoryHolder {
        private final List<Recipe> recipes;
        private final int page;
        private final ItemStack query;
        private final boolean usages;
        private final Map<Integer, ItemStack> searchableItems = new HashMap<>();
        private Inventory inventory;

        private Holder(List<Recipe> recipes, int page, ItemStack query, boolean usages) {
            this.recipes = recipes;
            this.page = page;
            this.query = query;
            this.usages = usages;
        }

        @Override public @NotNull Inventory getInventory() { return inventory; }
    }

    public static void open(Player player) {
        show(player, List.of(), 0, null, false);
    }

    public static void open(Player player, ItemStack item) {
        search(player, item, false);
    }

    private static void search(Player player, ItemStack item, boolean usages) {
        ItemStack query = item.clone();
        query.setAmount(1);
        List<Recipe> recipes = new ArrayList<>();
        List<Recipe> baseRecipes = new ArrayList<>();
        List<Recipe> unnamedRecipes = new ArrayList<>();
        ItemStack baseItem = withoutEnchantmentsAndDamage(query);
        ItemStack unnamedItem = baseItem.clone();
        ItemMeta unnamedMeta = unnamedItem.getItemMeta();
        unnamedMeta.customName(null);
        unnamedItem.setItemMeta(unnamedMeta);
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            ItemStack result = recipe.getResult();
            if (usages) {
                if (ingredients(recipe).stream().anyMatch(choice -> choice.test(query))) recipes.add(recipe);
            } else if (!result.getType().isAir()) {
                boolean dynamicMatch = recipe instanceof TransmuteRecipe transmute
                        && result.getType() == query.getType() && transmute.getInput().test(query);
                if (result.isSimilar(query) || dynamicMatch) recipes.add(recipe);
                else if (result.isSimilar(baseItem)) baseRecipes.add(recipe);
                else if (result.isSimilar(unnamedItem)) unnamedRecipes.add(recipe);
            }
        }
        if (!usages && recipes.isEmpty()) {
            recipes.addAll(baseRecipes.isEmpty() ? unnamedRecipes : baseRecipes);
        }
        show(player, List.copyOf(recipes), 0, query, usages);
    }

    private static ItemStack withoutEnchantmentsAndDamage(ItemStack item) {
        ItemStack base = item.clone();
        ItemMeta meta = base.getItemMeta();
        for (var enchantment : meta.getEnchants().keySet()) meta.removeEnchant(enchantment);
        if (meta instanceof Damageable damageable && damageable.hasDamage()) damageable.resetDamage();
        base.setItemMeta(meta);
        return base;
    }

    private static List<RecipeChoice> ingredients(Recipe recipe) {
        List<RecipeChoice> choices = new ArrayList<>();
        if (recipe instanceof ShapedRecipe shaped) choices.addAll(shaped.getChoiceMap().values());
        else if (recipe instanceof ShapelessRecipe shapeless) choices.addAll(shapeless.getChoiceList());
        else if (recipe instanceof TransmuteRecipe transmute) {
            choices.add(transmute.getInput());
            choices.add(transmute.getMaterial());
        }
        else if (recipe instanceof CookingRecipe<?> cooking) choices.add(cooking.getInputChoice());
        else if (recipe instanceof StonecuttingRecipe stonecutting) choices.add(stonecutting.getInputChoice());
        else if (recipe instanceof SmithingRecipe smithing) {
            if (smithing instanceof SmithingTransformRecipe transform) choices.add(transform.getTemplate());
            else if (smithing instanceof SmithingTrimRecipe trim) choices.add(trim.getTemplate());
            choices.add(smithing.getBase());
            choices.add(smithing.getAddition());
        }
        choices.removeIf(Objects::isNull);
        return choices;
    }

    private static void show(Player player, List<Recipe> recipes, int page, ItemStack query, boolean usages) {
        Holder holder = new Holder(recipes, page, query, usages);
        Inventory inv = Bukkit.createInventory(holder, 45, line("<#b8ffde>配方查询 <#71877a>· Recipe"));
        holder.inventory = inv;
        ItemStack frame = icon(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 45; i++) {
            if (i < 9 || i >= 36 || i % 9 == 0 || i % 9 == 8) inv.setItem(i, frame);
        }
        inv.setItem(INFO, icon(Material.KNOWLEDGE_BOOK, "<#b8ffde>配方查询",
                "<#aab8b0>点击背包或配方中的物品", "<#d8e3dc>左键 <#aab8b0>查看如何制作",
                "<#d8e3dc>右键 <#aab8b0>查看可以制作什么"));
        if (query == null) {
            inv.setItem(4, icon(Material.CRAFTING_TABLE, "<#ffd166>选择物品", "<#aab8b0>点击背包里的物品开始查询"));
        } else if (recipes.isEmpty()) {
            inv.setItem(4, icon(Material.BARRIER, "<#ffb4a8>没有找到配方",
                    usages ? "<#aab8b0>该物品没有找到可查询的用途" : "<#aab8b0>该物品没有找到制作配方"));
            inv.setItem(22, query.clone());
            holder.searchableItems.put(22, query.clone());
        } else {
            Recipe recipe = recipes.get(page);
            inv.setItem(4, workstation(recipe, page, recipes.size()));
            renderRecipe(inv, recipe);
            if (page > 0) inv.setItem(PREVIOUS, icon(Material.ARROW, "<#b8ffde>← 上一个配方"));
            if (page + 1 < recipes.size()) inv.setItem(NEXT, icon(Material.ARROW, "<#b8ffde>下一个配方 →"));
        }
        player.openInventory(inv);
    }

    private static ItemStack workstation(Recipe recipe, int page, int total) {
        Material material;
        String name;
        switch (recipe) {
            case BlastingRecipe blastingRecipe -> {
                material = Material.BLAST_FURNACE;
                name = "高炉";
            }
            case SmokingRecipe smokingRecipe -> {
                material = Material.SMOKER;
                name = "烟熏炉";
            }
            case CampfireRecipe campfireRecipe -> {
                material = Material.CAMPFIRE;
                name = "营火";
            }
            case FurnaceRecipe furnaceRecipe -> {
                material = Material.FURNACE;
                name = "熔炉";
            }
            case StonecuttingRecipe stonecuttingRecipe -> {
                material = Material.STONECUTTER;
                name = "切石机";
            }
            case SmithingRecipe smithingRecipe -> {
                material = Material.SMITHING_TABLE;
                name = "锻造台";
            }
            case TransmuteRecipe transmuteRecipe -> {
                material = Material.CRAFTING_TABLE;
                name = "工作台 · 物品转换";
            }
            case ShapedRecipe shapedRecipe -> {
                material = Material.CRAFTING_TABLE;
                name = "工作台 · 有序合成";
            }
            case ShapelessRecipe shapelessRecipe -> {
                material = Material.CRAFTING_TABLE;
                name = "工作台 · 无序合成";
            }
            case null, default -> {
                material = Material.BOOK;
                name = "特殊配方";
            }
        }
        List<String> lore = getLore(recipe, page, total);
        return icon(material, "<#ffd166>" + name, lore.toArray(String[]::new));
    }

    private static @NotNull List<String> getLore(Recipe recipe, int page, int total) {
        List<String> lore = new ArrayList<>(List.of("<#aab8b0>配方 <#ffd166>" + (page + 1) + " <#71877a>/ " + total));
        if (recipe instanceof CookingRecipe<?> cooking) {
            lore.add("<#aab8b0>加工时间 <#ffd166>" + cooking.getCookingTime() / 20.0 + " 秒");
        }
        if (recipe instanceof TransmuteRecipe) lore.add("<#aab8b0>产物保留输入物品的数据");
        if (recipe instanceof SmithingTrimRecipe) lore.add("<#aab8b0>产物外观取决于材料");
        if (!(recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe || recipe instanceof CookingRecipe<?>
                || recipe instanceof StonecuttingRecipe || recipe instanceof SmithingRecipe || recipe instanceof TransmuteRecipe)) {
            lore.add("<#ffb4a8>此类型的材料无法在此展示");
        }
        return lore;
    }

    private static void renderRecipe(Inventory inv, Recipe recipe) {
        if (recipe instanceof ShapedRecipe shaped) {
            Map<Character, RecipeChoice> map = shaped.getChoiceMap();
            String[] shape = shaped.getShape();
            for (int row = 0; row < Math.min(3, shape.length); row++) {
                for (int col = 0; col < Math.min(3, shape[row].length()); col++) {
                    putChoice(inv, GRID[row * 3 + col], map.get(shape[row].charAt(col)));
                }
            }
        } else if (recipe instanceof ShapelessRecipe shapeless) {
            List<RecipeChoice> choices = shapeless.getChoiceList();
            for (int i = 0; i < Math.min(GRID.length, choices.size()); i++) putChoice(inv, GRID[i], choices.get(i));
        } else if (recipe instanceof TransmuteRecipe transmute) {
            putChoice(inv, 19, transmute.getInput());
            putChoice(inv, 20, transmute.getMaterial());
        } else if (recipe instanceof CookingRecipe<?> cooking) putChoice(inv, 20, cooking.getInputChoice());
        else if (recipe instanceof StonecuttingRecipe stone) putChoice(inv, 20, stone.getInputChoice());
        else if (recipe instanceof SmithingRecipe smithing) {
            if (smithing instanceof SmithingTransformRecipe transform) putChoice(inv, 19, transform.getTemplate());
            else if (smithing instanceof SmithingTrimRecipe trim) putChoice(inv, 19, trim.getTemplate());
            putChoice(inv, 20, smithing.getBase());
            putChoice(inv, 21, smithing.getAddition());
        }
        inv.setItem(22, icon(Material.ARROW, "<#b8ffde>→ 产物"));
        ItemStack result = recipe.getResult();
        if (!result.getType().isAir()) {
            inv.setItem(25, result.clone());
            ((Holder) inv.getHolder()).searchableItems.put(25, result.clone());
        } else inv.setItem(25, icon(Material.BARRIER, "<#ffd166>动态产物", "<#aab8b0>产物取决于放入的材料"));
    }

    private static void putChoice(Inventory inv, int slot, RecipeChoice choice) {
        if (choice == null) return;
        List<ItemStack> options = new ArrayList<>();
        switch (choice) {
            case RecipeChoice.ExactChoice exact -> {
                for (ItemStack item : exact.getChoices()) options.add(item.clone());
            }
            case RecipeChoice.ItemTypeChoice itemTypes -> {
                var registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
                for (ItemType itemType : itemTypes.itemTypes().resolve(registry)) {
                    ItemStack item = itemType.createItemStack();
                    if (!item.getType().isAir()) options.add(item);
                }
            }
            case RecipeChoice.MaterialChoice materials -> {
                for (Material material : materials.getChoices()) {
                    if (!material.isLegacy() && material.isItem() && !material.isAir())
                        options.add(new ItemStack(material));
                }
            }
            default -> {
                // 未知的选择类型仍可通过其谓词发现普通材料。
                for (Material material : Material.values()) {
                    if (!material.isLegacy() && material.isItem() && !material.isAir()) {
                        ItemStack item = new ItemStack(material);
                        if (choice.test(item)) options.add(item);
                    }
                }
            }
        }
        if (options.isEmpty()) return;
        ItemStack display = options.getFirst().clone();
        ((Holder) inv.getHolder()).searchableItems.put(slot, display.clone());
        List<Component> alternatives = new ArrayList<>();
        for (ItemStack item : options) alternatives.add(item.displayName());
        if (alternatives.size() > 1) {
            ItemMeta meta = display.getItemMeta();
            List<Component> lore = meta.hasLore() ? new ArrayList<>(Objects.requireNonNull(meta.lore())) : new ArrayList<>();
            lore.add(line(" "));
            lore.add(line("<#b8ffde>可替代材料 <#71877a>(" + alternatives.size() + " 种)"));
            for (int i = 0; i < Math.min(8, alternatives.size()); i++) {
                lore.add(line("<#71877a>· <#d8e3dc>").append(alternatives.get(i)));
            }
            if (alternatives.size() > 8) lore.add(line("<#71877a>以及其他 " + (alternatives.size() - 8) + " 种材料"));
            meta.lore(lore);
            display.setItemMeta(meta);
        }
        inv.setItem(slot, display);
    }

    private static Component line(String text) { return miniMessage.deserialize("<!i>" + text); }

    private static ItemStack icon(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(line(name));
        List<Component> lines = new ArrayList<>();
        for (String entry : lore) lines.add(line(entry));
        meta.lore(lines);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Holder holder)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();
        if (slot >= holder.inventory.getSize() && event.getClickedInventory() == player.getInventory()) {
            if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) return;
            ItemStack selected = event.getCurrentItem();
            if (selected == null || selected.getType().isAir()) return;
            ItemStack query = selected.clone();
            boolean usages = event.getClick() == ClickType.RIGHT;
            later(player, holder, () -> search(player, query, usages));
        } else if (holder.searchableItems.containsKey(slot)
                && (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT)) {
            ItemStack query = holder.searchableItems.get(slot).clone();
            boolean usages = event.getClick() == ClickType.RIGHT;
            later(player, holder, () -> search(player, query, usages));
        } else if (slot == PREVIOUS && holder.page > 0) {
            later(player, holder, () -> show(player, holder.recipes, holder.page - 1, holder.query, holder.usages));
        } else if (slot == NEXT && holder.page + 1 < holder.recipes.size()) {
            later(player, holder, () -> show(player, holder.recipes, holder.page + 1, holder.query, holder.usages));
        }
    }

    private static void later(Player player, Holder holder, Runnable action) {
        Bukkit.getScheduler().runTask(instance, () -> {
            if (player.isOnline() && player.getOpenInventory().getTopInventory().equals(holder.inventory)) action.run();
        });
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof Holder) event.setCancelled(true);
    }
}
