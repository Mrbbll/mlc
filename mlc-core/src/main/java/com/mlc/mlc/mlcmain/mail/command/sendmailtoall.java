package com.mlc.mlc.mlcmain.mail.command;

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
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("该命令只能由玩家执行");
            return true;
        }
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(itemStack.getType()== Material.AIR)
        {
            commandSender.sendMessage(Component.text("手上物品为空", TextColor.color(0xFF4213)));
            return false;
        }
        try {
            int count = sendToAll(itemStack);
            player.sendMessage("已发送邮件给 " + count + " 名玩家");
        } catch (IOException e) {
            instance.getLogger().log(java.util.logging.Level.SEVERE, "群发邮件失败，部分玩家可能已收到邮件", e);
            player.sendMessage("群发邮件失败，部分玩家可能已收到邮件，请查看服务器日志");
        }
        return true;
    }

    /**
     * Sends a copy of the item to each player listed in usercache.json.
     * Must be called on the server thread; does not consume the supplied item.
     * If an IOException occurs, some players may already have received the item.
     *
     * @return the number of players sent mail
     */
    public static int sendToAll(@NotNull ItemStack item) throws IOException {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("sendToAll must be called on the server thread");
        }
        if (item == null || item.getType().isAir()) {
            throw new IllegalArgumentException("The mail item must not be empty");
        }
        List<String> playernames = new sendmailtoall().findplayernames();
        ItemStack copy = item.clone();
        sendmail mail = new sendmail();
        for (String playername : playernames) {
            mail.savetomailgui(copy, playername);
        }
        return playernames.size();
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
