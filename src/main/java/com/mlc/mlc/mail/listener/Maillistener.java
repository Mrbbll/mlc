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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.mlc.mlc.Mlc.instance;

public class Maillistener implements Listener {
    public static Map<UUID,Integer> mailnum = new HashMap<>();

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
            mailnum.put(player.getUniqueId(),mailnum.getOrDefault(player.getUniqueId(),0)-1);
            fileConfiguration.save(file);

            Mailgui.open(player,file);
            }
        }
    }
    @EventHandler
    public void onjion(PlayerJoinEvent join){

        if (haveunreadmail(join.getPlayer())) return;

        File mailDir = new File(instance.getDataFolder(), "mail");
        String string = join.getPlayer().getUniqueId() + ".yml";
        File file = new File(mailDir,string);
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("item");
        if (configurationSection != null && !configurationSection.getKeys(false).isEmpty()) {
            for(String key : configurationSection.getKeys(false)){
                mailnum.put(join.getPlayer().getUniqueId(),mailnum.getOrDefault(join.getPlayer().getUniqueId(),0)+1);
            }
            haveunreadmail(join.getPlayer());
        }
    }

    private static boolean haveunreadmail(Player player) {
        if(mailnum.containsKey(player.getUniqueId())){
            player.sendMessage(Component.text("你有" + mailnum.get(player.getUniqueId()) + "封信未读")
                    .color(TextColor.fromHexString("#7cff4d"))
                    .decoration(TextDecoration.BOLD,true)
            );
            return true;
        }
        return false;
    }
}
