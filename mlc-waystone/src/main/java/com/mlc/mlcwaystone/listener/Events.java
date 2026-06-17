package com.mlc.mlcwaystone.listener;


import com.mlc.mlcwaystone.waystonegui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;


import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.mlc.mlcwaystone.Mlcwaystone.*;
import static com.mlc.mlcwaystone.waystonegui.editinv;
import static com.mlc.mlcwaystone.waystonegui.inv;


public class Events implements Listener {


    @EventHandler(ignoreCancelled = true)
    public void placewaystone(PlayerInteractEvent event) throws IOException {

        //监听放置
        if(event.getClickedBlock() == null)
            return;

        if(!event.getBlockFace().equals(BlockFace.UP))
            return;

        Player player = event.getPlayer();
        if(player.getInventory().getItemInMainHand().isEmpty())
            return;

        ItemMeta item1 = player.getInventory().getItemInMainHand().getItemMeta();
        if(!item1.hasItemModel())
            return;

        if(!Objects.requireNonNull(item1.getItemModel()).toString().equals("mlc:waystone"))
            return;
        event.setCancelled(true);

        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        int num = fileConfiguration.getInt("num");
        if(num>=54){
            event.getPlayer().sendMessage("已超最大数量");
            return;
        }

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK&& event.getHand() == EquipmentSlot.HAND){


            Location loc = event.getClickedBlock().getLocation().clone().add(0.5, 1, 0.5);
            Location loc1 = loc.clone().add(0,1,0);
            Block block = loc.getBlock();
            Block block1 = loc1.getBlock();

            if (block.getType().equals(Material.AIR) && block1.getType().equals(Material.AIR)) {
                ArmorStand armorStand = (ArmorStand) event.getClickedBlock().getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
                armorStand.setSmall(true);
                armorStand.setVisible(false);
                armorStand.setInvulnerable(true);
                armorStand.setGravity(false);

                armorStand.getEquipment().setHelmet(waystoneitem);

                block.setType(Material.BARRIER);
                block1.setType(Material.BARRIER);

                FileConfiguration fileConfiguration_item = YamlConfiguration.loadConfiguration(itemsflie);
                saveloc(block,fileConfiguration,fileConfiguration_item);
                num++;
                player.sendMessage(Component.text("成功放置传送石").color(TextColor.fromHexString("#00ff33")));
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                int amount = itemStack.getAmount();
                amount--;
                itemStack.setAmount(amount);

                fileConfiguration.set("num",num);
                fileConfiguration.save(file);
                fileConfiguration_item.save(itemsflie);
                waystonegui.setitem(inv);
                waystonegui.setitem(editinv);
            }
        }
    }

    @EventHandler
    public void delwaystone(PlayerInteractEvent event) throws IOException {
        //监听破坏
        if(event.getAction() == Action.LEFT_CLICK_BLOCK&& Objects.equals(event.getHand(), EquipmentSlot.HAND)){

            Block block = Objects.requireNonNull(event.getClickedBlock()).getLocation().add(0.5, 0, 0.5).getBlock();

            if (block.getType().equals(Material.BARRIER)) {
                //点击下面方块
                for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation().add(0.5,0,0.5), 0.0, 0.01, 0.0)) {
//                    event.getPlayer().sendMessage("a");
                    if (entity instanceof ArmorStand) {
                        removewaystone(block,event,entity);
                        return;
                    }
                }
                //点击上面方块
                Block block1 = block.getLocation().add(0,-1,0).getBlock();
                for (Entity entity : block1.getWorld().getNearbyEntities(block1.getLocation().add(0.5,0,0.5), 0.0, 0.01, 0.0)) {
//                    event.getPlayer().sendMessage("b");
                    if (entity instanceof ArmorStand) {
                        removewaystone(block1,event,entity);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
    public  void opengui(PlayerInteractEvent event){
        //监听打开gui
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block block = Objects.requireNonNull(event.getClickedBlock());
            if (block.getType().equals(Material.BARRIER)) {
                //点击下面方块
                for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation().add(0.5, 0, 0.5), 0.0, 0.01, 0.0)) {
                    if (entity instanceof ArmorStand) {
                        openhandler(event);
                        return;
                    }
                }
                //点击上面方块
                Block block1 = block.getLocation().clone().add(0, -1, 0).getBlock();
                for (Entity entity : block1.getWorld().getNearbyEntities(block1.getLocation().add(0.5, 0, 0.5), 0.0, 0.01, 0.0)) {
                    if (entity instanceof ArmorStand) {
                        openhandler(event);
                        return;
                    }
                }
            }
        }
    }

    //打开gui处理
    private static void openhandler(PlayerInteractEvent event) {
        //如果是下蹲且是op，进入编辑模式
        if(event.getPlayer().isSneaking() && event.getPlayer().isOp()){
            Player player = event.getPlayer();
            player.sendMessage(Component.text("已进入编辑模式").color(TextColor.fromHexString("#00ff33")));
            waystonegui.openeditgui(player);
            event.setCancelled(true);
            return;
        }
        //如果不是op，只能打开gui
        waystonegui.openwaystonegui(event.getPlayer());
        event.setCancelled(true);
    }

    @EventHandler
    public void clikgui(InventoryClickEvent event) throws IOException {
//        InventoryView inventoryView = event.getView();
//        if(inventoryView.title().equals(Component.text("传送点"))){
        if(event.getInventory().equals(inv)){
            event.setCancelled(true);
            int num = event.getRawSlot();
            if(num<0||num>event.getInventory().getSize())
            {
                return;
            }
            ItemStack item = event.getCurrentItem();
            if(item == null)
            {
                return;
            }

            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("waystones");
            if (configurationSection != null) {
                Set<String> locids = configurationSection.getKeys(false);
                for(String locid :locids){
                    num--;
                    if(num==-1){
                        Map<String,Object> loc = Objects.requireNonNull(configurationSection.getConfigurationSection(locid)).getValues(false);
                        Location location = Location.deserialize(loc);
                        event.getWhoClicked().teleport(location.add(0.5,2,0.5));
                    }
                }
            }
        }
        else if(event.getInventory().equals(editinv)){
            event.setCancelled(true);
            int num = event.getRawSlot();
            if(num<0||num>event.getInventory().getSize())
            {
                return;
            }
            ItemStack item = event.getCurrentItem();
            if(item == null)
            {
                return;
            }

            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(itemsflie);
            ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("items");
            if (configurationSection != null) {
                Set<String> locids = configurationSection.getKeys(false);
                for(String locid :locids){
                    num--;
                    if(num==-1){
                        ItemStack itemstack = event.getWhoClicked().getInventory().getItemInMainHand();
                        ItemMeta itemMeta = itemstack.getItemMeta();
                        if(itemMeta == null){
                            event.getWhoClicked().sendMessage(Component.text("请手持物品").color(TextColor.fromHexString("#ff3300")));
                            return;
                        }

                        String type = itemstack.getType().toString();
                        String itemname = PlainTextComponentSerializer.plainText().serialize(itemMeta.itemName());
                        if(itemMeta.customName()!=null){
                            itemname = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(itemMeta.customName()));
                        }
                        configurationSection.set(locid+".type",type);
                        configurationSection.set(locid+".itemname",itemname);
                        fileConfiguration.save(itemsflie);
                        waystonegui.setitem(inv);
                        waystonegui.setitem(editinv);
                        break;
                    }
                }
            }
        }
    }


    public void removewaystone(Block block,PlayerInteractEvent event,Entity entity) throws IOException {
        if(!event.getPlayer().isOp()){
            return;
        }
        Block block1 = block.getLocation().clone().add(0,1,0).getBlock();
        block.setType(Material.AIR);
        block1.setType(Material.AIR);
        entity.remove();
        event.getPlayer().sendMessage(Component.text("成功移除传送石").color(TextColor.fromHexString("#ff3300")));
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        FileConfiguration fileConfiguration_item =YamlConfiguration.loadConfiguration(itemsflie);
        int num = fileConfiguration.getInt("num");
        block1.getWorld().dropItem(block1.getLocation(),waystoneitem);
        delloc(block,fileConfiguration,fileConfiguration_item);
        num--;
        if(num==-1){
            num=0;
        }
        fileConfiguration.set("num",num);
        fileConfiguration.save(file);
        fileConfiguration_item.save(itemsflie);
        waystonegui.setitem(inv);
        waystonegui.setitem(editinv);
    }

    public void saveloc(Block block,FileConfiguration fileConfiguration,FileConfiguration fileConfiguration_item){
        long timeMillis = System.currentTimeMillis();
        @NotNull Map<String, Object> stoneloc = block.getLocation().serialize();
        fileConfiguration.set("waystones."+ timeMillis,stoneloc);

        String type = "CAMPFIRE";
        String itemname = "未命名传送点";

        fileConfiguration_item.set("items." + timeMillis + ".type",type);
        fileConfiguration_item.set("items." + timeMillis + ".itemname",itemname);

    }

    public void delloc(Block block, FileConfiguration fileConfiguration,FileConfiguration fileConfiguration1){
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("waystones");
        ConfigurationSection configurationSection1 = fileConfiguration1.getConfigurationSection("items");
        if (configurationSection != null) {
            Set<String> locids = configurationSection.getKeys(false);
            for(String locid:locids){
                ConfigurationSection locConfig = configurationSection.getConfigurationSection(locid);
                Location blockloc = block.getLocation();
                if (locConfig != null && blockloc.equals(Location.deserialize(locConfig.getValues(false)))){
                    configurationSection.set(locid,null);
                    if (configurationSection1 != null) {
                        configurationSection1.set(locid,null);
                    }
                    return;
                }
            }
        }
    }

}
