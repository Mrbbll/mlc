package com.mlc.mlc.mlcmain.items.itemfunc.create.crates;

import com.mlc.mlc.mlcmain.items.itemmannager.Cratesitems;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

public class Crates {
    public static Inventory inventory;
    public  static  List<ItemStack> booklist;
    public static PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();

    private static final List<Material> TRIM_TEMPLATES = materialsEndingWith("_ARMOR_TRIM_SMITHING_TEMPLATE");
    private static final List<Material> POTTERY_SHERDS = materialsEndingWith("_POTTERY_SHERD");
    private static final List<Material> BANNER_PATTERNS = materialsEndingWith("_BANNER_PATTERN");

    public static void getitems(Player player){
        java.util.Random random = new java.util.Random();
        //随机进入3个物品池，概率为70% 27% 3%
        int randomnum = random.nextInt(100);
        if(randomnum < 70){
            randomnum = random.nextInt(Cratesitems.t3list.size());
            //判断背包满没，满了掉地上
            Integer itemid = Cratesitems.t3list.get(randomnum);
            giveitem(player, itemid);
        }else if(randomnum < 97){
            randomnum = random.nextInt(Cratesitems.t2list.size());
            Integer itemid = Cratesitems.t2list.get(randomnum);
            giveitem(player, itemid);
        }else{
            randomnum = random.nextInt(Cratesitems.t1list.size());
            Integer itemid = Cratesitems.t1list.get(randomnum);
            giveitem(player, itemid);
        }


    }

    private static void giveitem(Player player, Integer itemid) {
        ItemStack item = Cratesitems.itemsmap.get(itemid);


        if(serializer.serialize(item.effectiveName()).equals("随机附魔书")){
            item = getrandombook();
        }else if(serializer.serialize(item.effectiveName()).equals("随机纹饰")) {
            item = getrandomtrimpattern();
        }else if(serializer.serialize(item.effectiveName()).equals("随机陶罐碎片")){
            item = getrandompotterysherd();
        }else if(serializer.serialize(item.effectiveName()).equals("随机旗帜图案")){
            item = getrandombannerpattern();
        }

        giveitem(player, item);
        player.sendMessage(serializer.serialize(item.effectiveName()));
        player.sendMessage(Component.text("你获得了").append(item.effectiveName()));
    }

    private static ItemStack getrandompotion() {
        Random random = new Random();
        int randompotion = random.nextInt(10);
        return new ItemStack(Material.POTION);
    }

    private static ItemStack getrandombook() {
        ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemMeta;
        List<Enchantment> enchantments = RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT).stream().toList();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Enchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
        int level = random.nextInt(enchantment.getStartLevel(), enchantment.getMaxLevel() + 1);
        enchantmentStorageMeta.addStoredEnchant(enchantment, level, false);
        itemStack.setItemMeta(enchantmentStorageMeta);
        return itemStack;

    }

    public static ItemStack getrandomtrimpattern() {
        return randomItem(TRIM_TEMPLATES);
    }

    public static ItemStack getrandompotterysherd(){
        return randomItem(POTTERY_SHERDS);
    }

    public static ItemStack getrandombannerpattern(){
        return randomItem(BANNER_PATTERNS);
    }

    private static List<Material> materialsEndingWith(String suffix) {
        return Arrays.stream(Material.values())
                .filter(Material::isItem)
                .filter(material -> material.name().endsWith(suffix))
                .toList();
    }

    private static ItemStack randomItem(List<Material> materials) {
        return new ItemStack(materials.get(ThreadLocalRandom.current().nextInt(materials.size())));
    }

    private static void giveitem(Player player, ItemStack item) {
        if(player.getInventory().firstEmpty() == -1){
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }else{
            player.give(item);
        }
    }
}
