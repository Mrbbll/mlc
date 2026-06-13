package com.mlc.mlcwaystone.commands;

import com.mlc.mlcwaystone.waystonegui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static com.mlc.mlcwaystone.Mlcwaystone.instance;
import static com.mlc.mlcwaystone.Mlcwaystone.file;
import static com.mlc.mlcwaystone.Mlcwaystone.itemsflie;
import static com.mlc.mlcwaystone.waystonegui.editinv;
import static com.mlc.mlcwaystone.waystonegui.inv;


public class Reload implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(sender instanceof Player player && !player.isOp()){
            return false;
        }
        file = new File(instance.getDataFolder(), "waystone/location.yml");
        itemsflie = new File(instance.getDataFolder(), "waystone/displayitems.yml");
        Component text = Component.text("传送点");
        inv = Bukkit.createInventory(null,9*6,text);
        text = Component.text("传送点编辑");
        editinv = Bukkit.createInventory(null,9*6,text);
        waystonegui.setitem(inv);
        waystonegui.setitem(editinv);
        return false;
    }
}
