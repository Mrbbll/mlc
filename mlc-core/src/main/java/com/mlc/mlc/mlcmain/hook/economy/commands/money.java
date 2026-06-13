package com.mlc.mlc.mlcmain.hook.economy.commands;

import com.mlc.mlc.mlcmain.hook.economy.Moneyfilemanager;
import com.mlc.mlc.mlcmain.items.itemmannager.Cratesitems;
import com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.mlc.mlc.mlcmain.hook.economy.Moneyfilemanager.playermoneyMap;
import static com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems.*;

public class money implements TabExecutor {
    private Player target;
    private int money;
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(!(commandSender instanceof Player player)){
            commandSender.sendMessage("不是玩家");
            return false;
        }

        if(strings.length == 0){
            commandSender.sendMessage("你的水晶币为: " + Moneyfilemanager.getPlayerMoney(player.getUniqueId()));
            return false;
        }

        switch (strings[0]) {
            case "money":
                commandSender.sendMessage("你的水晶币为: " + Moneyfilemanager.getPlayerMoney(player.getUniqueId()));
                break;
            case "top":
                commandSender.sendMessage("货币排行榜: " + Moneyfilemanager.getTopPlayers());
                break;
            case "give":
                if(!player.isOp()){
                    return false;
                }

                if(strings.length != 3){
                    commandSender.sendMessage("用法: /money give <玩家> <金额>");
                    return false;
                }

                target = player.getServer().getPlayer(strings[1]);
                if(target == null){
                    commandSender.sendMessage("玩家不存在");
                    return false;
                }
                if(!strings[2].matches("\\d+")){
                    commandSender.sendMessage("数量必须为整数");
                    return false;
                }
                money = Integer.parseInt(strings[2]);
                if(money <= 0){
                    commandSender.sendMessage("数量必须大于0");
                    return false;
                }
                Moneyfilemanager.setPlayerMoney(target.getUniqueId(), Moneyfilemanager.getPlayerMoney(target.getUniqueId()) + money);
                commandSender.sendMessage("成功给玩家 " + target.getName() + " 给予 " + money + " 水晶币");
                break;
            case "set":
                if(!player.isOp()){
                    return false;
                }
                if(strings.length != 3){
                    commandSender.sendMessage("用法: /money set <玩家> <金额>");
                    return false;
                }
                target = player.getServer().getPlayer(strings[1]);
                if(target == null){
                    commandSender.sendMessage("玩家不存在");
                    return false;
                }
                if(!strings[2].matches("\\d+")){
                    commandSender.sendMessage("数量必须为整数");
                    return false;
                }
                money = Integer.parseInt(strings[2]);
                if(money <= 0){
                    commandSender.sendMessage("数量必须大于0");
                    return false;
                }
                Moneyfilemanager.setPlayerMoney(target.getUniqueId(), money);
                commandSender.sendMessage("成功设置玩家 " + target.getName() + " 的水晶币为 " + money);
                break;
            case "pay":
                if(strings.length != 3){
                    commandSender.sendMessage("用法: /money pay <玩家> <金额>");
                    return false;
                }
                target = player.getServer().getPlayer(strings[1]);

                if(target == null){
                    commandSender.sendMessage("玩家不存在");
                    return false;
                }
                if(!strings[2].matches("\\d+")){
                    commandSender.sendMessage("数量必须为整数");
                    return false;
                }
                money = Integer.parseInt(strings[2]);
                if(money <= 0){
                    commandSender.sendMessage("数量必须大于0");
                    return false;
                }
                if(target == player){
                    commandSender.sendMessage("不能给自己支付水晶币");
                    return false;
                }
                if(playermoneyMap.getOrDefault(player.getUniqueId(),0) < money){
                    commandSender.sendMessage("你没有足够的水晶币");
                    return false;
                }
                Moneyfilemanager.setPlayerMoney(player.getUniqueId(), Moneyfilemanager.getPlayerMoney(player.getUniqueId()) - money);
                Moneyfilemanager.setPlayerMoney(target.getUniqueId(), Moneyfilemanager.getPlayerMoney(target.getUniqueId()) + money);

                commandSender.sendMessage("成功给玩家 " + target.getName() + " 支付 " + money + " 水晶币");
                break;
            case "save":
                player.sendMessage(Component.text("你当前有"+Moneyfilemanager.getPlayerMoney(player.getUniqueId())+"水晶碎块"));
                ItemStack item = player.getInventory().getItemInMainHand();
                if(Objects.equals(item.getItemMeta(), money_ingot.getItemMeta())) {
                    int money = item.getAmount();
                    player.sendMessage(Component.text("存入" + money + "水晶币").color(NamedTextColor.GREEN));
                    Moneyfilemanager.setPlayerMoney(player.getUniqueId(), Moneyfilemanager.getPlayerMoney(player.getUniqueId()) + money);
                    item.setAmount(0);
                }else if(Objects.equals(item.getItemMeta(), money_stack.getItemMeta())) {
                    int money = item.getAmount() * 9;
                    player.sendMessage(Component.text("存入" + money + "水晶币").color(NamedTextColor.GREEN));
                    Moneyfilemanager.setPlayerMoney(player.getUniqueId(), Moneyfilemanager.getPlayerMoney(player.getUniqueId()) + money);
                    item.setAmount(0);
                }else if(Objects.equals(item.getItemMeta(), money_coin.getItemMeta())) {
                    int money = item.getAmount();
                    player.sendMessage(Component.text("存入" + money + "水晶币").color(NamedTextColor.GREEN));
                    Moneyfilemanager.setPlayerMoney(player.getUniqueId(), Moneyfilemanager.getPlayerMoney(player.getUniqueId()) + money);
                    item.setAmount(0);
                }
                break;
            case "drop":
                if(Moneyfilemanager.getPlayerMoney(player.getUniqueId()) >= 64){
                    ItemStack coin = money_coin.clone();
                    coin.setAmount(64);
                    if(player.getInventory().firstEmpty() == -1){
                        player.getWorld().dropItemNaturally(player.getLocation(), coin);
                        Moneyfilemanager.setPlayerMoney(player.getUniqueId(), Moneyfilemanager.getPlayerMoney(player.getUniqueId()) - 64);
                        player.sendMessage(
                                Component.text("取出64个水晶币,剩余")
                                        .append(Component.text(Moneyfilemanager.getPlayerMoney(player.getUniqueId()))).color(NamedTextColor.GREEN)
                                        .append(Component.text("个")));
                    }else {
                        player.give(coin);
                        Moneyfilemanager.setPlayerMoney(player.getUniqueId(), Moneyfilemanager.getPlayerMoney(player.getUniqueId()) - 64);
                        player.sendMessage(
                                Component.text("取出64个水晶币,剩余")
                                        .append(Component.text(Moneyfilemanager.getPlayerMoney(player.getUniqueId()))).color(NamedTextColor.GREEN)
                                        .append(Component.text("个")));
                    }
                    break;
                }else {
                    int num = Moneyfilemanager.getPlayerMoney(player.getUniqueId());
                    if(num<=0){
                        player.sendMessage("余额不足");
                        return false;
                    }
                    ItemStack coin = money_coin.clone();
                    coin.setAmount(num);
                    if(player.getInventory().firstEmpty() == -1){
                        player.getWorld().dropItemNaturally(player.getLocation(), coin);
                        Moneyfilemanager.setPlayerMoney(player.getUniqueId(), Moneyfilemanager.getPlayerMoney(player.getUniqueId()) - num);
                        player.sendMessage(
                                Component.text("取出"+num+"个水晶币,剩余")
                                        .append(Component.text(Moneyfilemanager.getPlayerMoney(player.getUniqueId()))).color(NamedTextColor.GREEN)
                                        .append(Component.text("个")));
                    }else {
                        player.give(coin);
                        Moneyfilemanager.setPlayerMoney(player.getUniqueId(), Moneyfilemanager.getPlayerMoney(player.getUniqueId()) - num);
                        player.sendMessage(
                                Component.text("取出"+num+"个水晶币,剩余")
                                        .append(Component.text(Moneyfilemanager.getPlayerMoney(player.getUniqueId()))).color(NamedTextColor.GREEN)
                                        .append(Component.text("个")));
                    }
                    break;
                }
    }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of("money","top","set","pay","save","drop");
    }
}
