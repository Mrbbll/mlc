package com.mlc.mlc.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mlc.mlc.Mlc.instance;

public class sendmailtoall implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        List<String> playernames;
        try {
            playernames = findplayernames();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Player player = (Player) commandSender;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(itemStack.getType()== Material.AIR)
        {
            commandSender.sendMessage(Component.text("手上物品为空", TextColor.color(0xFF4213)));
            return false;
        }
//异步
        List<String> finalPlayernames = playernames;
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            for(String playername : finalPlayernames)
            {
                player.sendMessage("尝试发送信件到" + playername);
                sendmail sendmail_1 = new sendmail();
                try {
                    sendmail_1.savetomailgui(itemStack,playername);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });


        return true;
    }

    public List<String> findplayernames() throws IOException {
        File serverfolder = instance.getServer().getWorldContainer().getParentFile();
        File file = new File(serverfolder, "usercache.json");
        String json = Files.readString(file.toPath());
        List<String> names = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()) {
            names.add(matcher.group(1));
        }
        return names;
    }
}
