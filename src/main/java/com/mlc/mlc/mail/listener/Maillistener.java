package com.mlc.mlc.mail.listener;

import com.mlc.mlc.mail.mailgui.Mailgui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.mlc.mlc.Mlc.instance;

public class Maillistener implements Listener {
    @EventHandler
    //邮箱事件监听
        public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent e) throws IOException {
        Player player = (Player) e.getWhoClicked();
        InventoryView inv = player.getOpenInventory();
        if(inv.title().equals(Component.text("邮箱", TextColor.fromHexString("#66ee1d"), TextDecoration.BOLD))){
        e.setCancelled(true);
        ItemStack itemStack =e.getCurrentItem();
        if(e.getRawSlot()<0||e.getRawSlot()>=e.getInventory().getSize())
        {
            return;
        };
        if(itemStack==null)
        {
            return;
        }
        else{
            player.give(itemStack);
            int slot = e.getRawSlot();
            String string = player.getUniqueId() + ".yml";
            File mailDir = new File(instance.getDataFolder(), "mail");
            File file = new File(mailDir, string);
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("item");
            List<String> items = null;
            if (configurationSection != null) {
                items = configurationSection.getKeys(false).stream().toList();
            }
            if (items != null) {
                configurationSection.set(items.get(slot),null);
            }
            fileConfiguration.save(file);

            Mailgui.open(player,file);
        }
    }
}
}
