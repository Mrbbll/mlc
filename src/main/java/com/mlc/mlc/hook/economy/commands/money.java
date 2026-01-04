package com.mlc.mlc.hook.economy.commands;

import com.mlc.mlc.hook.economy.Moneyfilemanager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.mlc.mlc.hook.economy.Moneyfilemanager.playermoneyMap;

public class money implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(!(commandSender instanceof Player player)){
            commandSender.sendMessage("不是玩家");
            return false;
        }

        if(strings.length == 0){
            commandSender.sendMessage("你的货币为: " + Moneyfilemanager.getPlayerMoney(player.getUniqueId()));
            return false;
        }

        switch (strings[0]) {
            case "money":
                commandSender.sendMessage("你的货币为: " + Moneyfilemanager.getPlayerMoney(player.getUniqueId()));
                break;
            case "top":
//                commandSender.sendMessage("货币排行榜: " + Moneyfilemanager.getTopPlayers());
                break;
            case "give":
                if(!player.isOp()){
                    return false;
                }

                if(strings.length != 3){
                    commandSender.sendMessage("用法: /money give <玩家> <金额>");
                    return false;
                }
                Player target = player.getServer().getPlayer(strings[1]);
                if(target == null){
                    commandSender.sendMessage("玩家不存在");
                    return false;
                }
                int money = Integer.parseInt(strings[2]);
                if(money <= 0){
                    commandSender.sendMessage("金额必须大于0");
                    return false;
                }
                Moneyfilemanager.setPlayerMoney(target.getUniqueId(), Moneyfilemanager.getPlayerMoney(target.getUniqueId()) + money);
                commandSender.sendMessage("成功给玩家 " + target.getName() + " 给予 " + money + " 货币");
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
                money = Integer.parseInt(strings[2]);
                if(money <= 0){
                    commandSender.sendMessage("金额必须大于0");
                    return false;
                }
                Moneyfilemanager.setPlayerMoney(target.getUniqueId(), money);
                commandSender.sendMessage("成功设置玩家 " + target.getName() + " 的货币为 " + money);
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
                money = Integer.parseInt(strings[2]);
                if(money <= 0){
                    commandSender.sendMessage("金额必须大于0");
                    return false;
                }
                if(playermoneyMap.get(player.getUniqueId()) < money){
                    commandSender.sendMessage("你没有足够的货币");
                    return false;
                }
                Moneyfilemanager.setPlayerMoney(player.getUniqueId(), Moneyfilemanager.getPlayerMoney(player.getUniqueId()) - money);
                Moneyfilemanager.setPlayerMoney(target.getUniqueId(), Moneyfilemanager.getPlayerMoney(target.getUniqueId()) + money);

                commandSender.sendMessage("成功给玩家 " + target.getName() + " 支付 " + money + " 货币");
                break;
        }
        return false;
    }
}
