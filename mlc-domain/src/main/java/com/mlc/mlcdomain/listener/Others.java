package com.mlc.mlcdomain.listener;

import com.mlc.mlcdomain.Mlcdomain;
import com.mlc.mlcdomain.dataManager.Databasemanager;
import com.mlc.mlcdomain.dataManager.DomainData;
import com.mlc.mlcdomain.dataManager.Domainmanager;
import com.mlc.mlcdomain.dataManager.Playerlastloc;
import com.mlc.mlcdomain.uilts.Creatdomain;
import com.mlc.mlcdomain.uilts.Renamedomain;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

import static com.mlc.mlcdomain.Mlcdomain.instance;
import static com.mlc.mlcdomain.Mlcdomain.miniMessage;
import static org.bukkit.Bukkit.getLogger;

public class Others implements Listener {
    @EventHandler
    public void rightclick(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR){
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if(!player.isSneaking()){
            return;
        }
        if(item.getType() == Material.AIR){
            return;
            //创建领地
        } else if (item.getType() == Material.WOODEN_HOE) {
            String domainName = "";
            if(!item.getItemMeta().hasDisplayName()){
                domainName = player.getName();
            }
            else {
                domainName = miniMessage.serialize(Objects.requireNonNull(item.getItemMeta().displayName()));
            }
            Creatdomain.createDomain(player, domainName);

            //重命名领地
        } else if(item.getType() ==Material.NAME_TAG){
            String domainName = "";
            if(!item.getItemMeta().hasDisplayName()){
                return;
            }
            else {
                domainName = miniMessage.serialize(Objects.requireNonNull(item.getItemMeta().displayName()));
                DomainData domainData = Databasemanager.getDomainAt(player.getWorld().getName(), player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());
                if(domainData == null){
                    player.sendMessage(Component.text("你不在领地内！")
                            .color(TextColor.fromHexString("#FF0000")));
                    return;
                }
                if(!domainData.getPlayerName().equals(player.getName())){
                    player.sendMessage(Component.text("你不是领地所有者！")
                            .color(TextColor.fromHexString("#FF0000")));
                    return;
                }
                String oldname = domainData.getDomain();
                Renamedomain.renameDomain(player, domainData, domainName);
                instance.getLogger().info("玩家" + player.getName() + "重命名了领地：" + oldname + "为" + domainName);
            }
            //删除领地
        } else if (item.getType()==Material.BRUSH) {
            DomainData domainData = Databasemanager.getDomainAt(player.getWorld().getName(), player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());

            if(domainData == null){
                player.sendMessage(Component.text("你不在领地内！")
                        .color(TextColor.fromHexString("#FF0000")));
                return;
            }
            if(!domainData.getPlayerName().equals(player.getName())){
                player.sendMessage(Component.text("你不是领地所有者！")
                        .color(TextColor.fromHexString("#FF0000")));
                return;
            }
            if(Databasemanager.deleteDomain(player.getWorld().getName(), player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), player.getUniqueId())){
                Domainmanager.deldomaingiveback(player);
                player.sendMessage(Component.text("删除领地成功")
                        .color(TextColor.fromHexString("#00FF00")));
                return;
            }
            else {
                player.sendMessage(Component.text("删除领地失败")
                        .color(TextColor.fromHexString("#FF0000")));
            }

            //改领地权限
        } else if (item.getType() == Material.ARROW){
            DomainData domainData = Databasemanager.getDomainAt(player.getWorld().getName(), player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());
             if(domainData == null){
                player.sendMessage(Component.text("你不在领地内！")
                        .color(TextColor.fromHexString("#FF0000")));
                return;
            }
            if(!domainData.getPlayerName().equals(player.getName())){
                player.sendMessage(Component.text("你不是领地所有者！")
                        .color(TextColor.fromHexString("#FF0000")));
                return;
            }
            int oldLevel = domainData.getLevel();

            int newLevel = (oldLevel + 1)%5;
            if(!Databasemanager.updateDomain(domainData, newLevel)){
                player.sendMessage(Component.text("更改领地权限失败")
                        .color(TextColor.fromHexString("#FF0000")));
                return;
            }

//            switch (oldLevel){
//                case 0:
//                    player.sendMessage(Component.text("当前区块权限为：无更改")
//                            .color(TextColor.fromHexString("#00FF00")));
//                    break;
//                case 1:
//                    player.sendMessage(Component.text("当前区块权限为：其他人不得破坏和放置（默认，一般机器可以开这个）")
//                            .color(TextColor.fromHexString("#00ffcb")));
//                    break;
//                case 2:
//                    player.sendMessage(Component.text("当前区块权限为：其他人不得交互")
//                            .color(TextColor.fromHexString("#fb00ff")));
//                    break;
//                case 3:
//                    player.sendMessage(Component.text("当前区块权限为：其他人不得交互（观赏性建筑使用）")
//                            .color(TextColor.fromHexString("#ff3c00")));
//                    break;
//            }

            switch (newLevel){
                case 0:
                    player.sendMessage(Component.text("将区块权限更改为：无更改")
                            .color(TextColor.fromHexString("#00FF00")));
                    break;
                case 1:
                    player.sendMessage(Component.text("将区块权限更改为：其他人不得破坏和放置（默认，一般机器可以开这个）")
                            .color(TextColor.fromHexString("#00ffcb")));
                    break;
                case 2:
                    player.sendMessage(Component.text("将区块权限更改为：其他人不得交互")
                            .color(TextColor.fromHexString("#fb00ff")));
                    break;
                case 3:
                    player.sendMessage(Component.text("将区块权限更改为：其他人不得交互（观赏性建筑使用）")
                            .color(TextColor.fromHexString("#ff3c00")));
                    break;
            }

        }
    }

    @EventHandler
    public void rightclickplayer(PlayerInteractEntityEvent event){
        if(!(event.getRightClicked() instanceof Player target)){
            return;
        }
        if(!event.getHand().equals(EquipmentSlot.HAND)){
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        //给玩家权限
        if (item.getType()==Material.BLAZE_ROD) {
            DomainData domainData = Databasemanager.getDomainAt(player.getWorld().getName(), player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());
            if(domainData == null){
                player.sendMessage(Component.text("你不在领地内！")
                        .color(TextColor.fromHexString("#FF0000")));
                return;
            }
            if(!domainData.getPlayerName().equals(player.getName())){
                player.sendMessage(Component.text("你不是领地所有者！")
                        .color(TextColor.fromHexString("#FF0000")));
                return;
            }
            if(player.isSneaking()){
                if(!Databasemanager.deleteMember(target.getUniqueId(), player.getUniqueId())){
                    player.sendMessage(Component.text("删除权限失败")
                            .color(TextColor.fromHexString("#FF0000")));
                    return;
                }
                player.sendMessage(Component.text("删除权限成功")
                        .color(TextColor.fromHexString("#00FF00")));
            }else{
                if(!Databasemanager.addMember(target.getUniqueId(), player.getUniqueId())){
                    player.sendMessage(Component.text("添加权限失败")
                            .color(TextColor.fromHexString("#FF0000")));
                    return;
                }
                player.sendMessage(Component.text("添加权限成功")
                        .color(TextColor.fromHexString("#00FF00")));
                target.sendMessage(Component.text("你被添加为成员")
                        .color(TextColor.fromHexString("#00FF00")));
            }
        }
    }

    @EventHandler
    public void onplayerjoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Playerlastloc.playerlastloc.put(player.getUniqueId(), player.getLocation());
    }

    @EventHandler
    public void playermove(PlayerMoveEvent event) {
        // 只有玩家跨越了chunk边界才检查领地
        Location from = event.getFrom();
        Location to = event.getTo();

        int fromX = from.getChunk().getX(), fromZ = from.getChunk().getZ();
        int toX = to.getChunk().getX(), toZ = to.getChunk().getZ();

        if (fromX == toX && fromZ == toZ && from.getWorld().equals(to.getWorld())) {
            return;
        }

        Player player = event.getPlayer();
        DomainData domainData = Databasemanager.getDomainAt(player.getWorld().getName(), toX, toZ);
        DomainData lastDomainData = Databasemanager.getDomainAt(player.getWorld().getName(), fromX, fromZ);

        // 进入领地
        if (domainData != null && lastDomainData == null) {
            player.sendMessage(Component.text("你进入了领地：" + domainData.getDomain() + "   owner:" + domainData.getPlayerName())
                    .color(TextColor.fromHexString("#ffb000")));
        }
        // 离开领地
        else if (lastDomainData != null && domainData == null) {
            player.sendMessage(Component.text("你离开了领地：" + lastDomainData.getDomain() + "   owner:" + lastDomainData.getPlayerName())
                    .color(TextColor.fromHexString("#ffb000")));
        }
        // 在不同领地间切换
        else if (domainData != null && lastDomainData != null && !domainData.getDomain().equals(lastDomainData.getDomain())) {
            player.sendMessage(Component.text("你进入了领地：" + domainData.getDomain() + "   owner:" + domainData.getPlayerName())
                    .color(TextColor.fromHexString("#ffb000")));
        }
    }
}

