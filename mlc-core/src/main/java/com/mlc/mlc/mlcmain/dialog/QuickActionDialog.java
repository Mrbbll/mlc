package com.mlc.mlc.mlcmain.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.DialogKeys;
import io.papermc.paper.registry.keys.tags.DialogTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import java.util.List;

public final class QuickActionDialog {
    private static final TypedKey<Dialog> KEY = key("quick_action_test");
    private static final TypedKey<Dialog> TRAVEL = key("quick_action_travel");
    private static final TypedKey<Dialog> FEATURES = key("quick_action_features");
    private static final TypedKey<Dialog> ENCHANTMENTS = key("quick_action_enchantments");
    private static final TypedKey<Dialog> ALTAR = key("quick_action_altar");

    private QuickActionDialog() {
    }

    private static TypedKey<Dialog> key(String name) {
        return DialogKeys.create(Key.key("mlc", name));
    }

    public static void register(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(RegistryEvents.DIALOG.compose(), event -> {
            event.registry().register(TRAVEL, builder -> builder
                    .base(page("传送与家", " /rtp 随机传送\n /sethome 设置家\n /home 回到家\n /back 返回上一个路径点\n\n /tpa <玩家> 请求传送到对方\n /tpahere <玩家> 请求对方传送过来\n /tpaccept 接受传送请求"))
                    .type(DialogType.notice()));
            event.registry().register(FEATURES, builder -> builder
                    .base(page("常用功能", " /menu 打开功能菜单\n /recipe 查询配方和用途\n /shop 打开建材商店\n /mail 打开邮箱\n /mail send <玩家> 发送邮件\n /money pay <玩家> <金额> 转账\n /item 在聊天中展示手持物品"))
                    .type(DialogType.notice()));
            event.registry().register(ENCHANTMENTS, builder -> builder
                    .base(page("附魔突破", "开放突破的魔咒及最高等级：\n 锋利 VII　效率 VII\n 时运 V　保护 VI\n 连锁挖矿 III\n\n突破需要相同的附魔书，且魔咒必须适用于目标物品，不能与已有魔咒冲突。\n其他魔咒不能使用此功能。"))
                    .type(DialogType.notice()));
            event.registry().register(ALTAR, builder -> builder
                    .base(page("突破祭坛与操作", "祭坛顶部为幽匿尖啸体，下方依次是哭泣的黑曜石和下界合金块。\n最底层以中心为基准铺 3×3 下界合金块，四个对角外侧放幽匿催发体。\n在哭泣的黑曜石同一高度，距中心两格的东南西北各放一个充满能量的重生锚。\n\n快捷栏第 1 格放下界之星，第 2 格放装备，第 3、4 格放两本完全相同的附魔书。手持第 1 格的下界之星点击尖啸体。\n成功后材料会消耗，装备将在祭坛上方掉落。"))
                    .type(DialogType.notice()));
            event.registry().register(KEY, builder -> builder
                    .base(DialogBase.builder(Component.text("服务器功能查询"))
                            .body(List.of(DialogBody.plainMessage(Component.text("选择一个分类查看服务器功能。ai生成的内容，仅测试"))))
                            .build())
                    .type(DialogType.dialogList(RegistrySet.keySet(RegistryKey.DIALOG,
                            TRAVEL, FEATURES, ENCHANTMENTS, ALTAR)).columns(2).buttonWidth(150).build()));
        });
        context.getLifecycleManager().registerEventHandler(
                LifecycleEvents.TAGS.preFlatten(RegistryKey.DIALOG), event ->
                        event.registrar().addToTag(DialogTagKeys.QUICK_ACTIONS, List.of(TagEntry.valueEntry(KEY))));
    }

    private static DialogBase page(String title, String text) {
        return DialogBase.builder(Component.text(title))
                .body(List.of(DialogBody.plainMessage(Component.text(text), 300)))
                .build();
    }
}
