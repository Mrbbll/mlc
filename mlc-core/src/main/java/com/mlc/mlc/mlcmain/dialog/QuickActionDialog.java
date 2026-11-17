package com.mlc.mlc.mlcmain.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.DialogKeys;
import io.papermc.paper.registry.keys.tags.DialogTagKeys;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import java.util.List;

public final class QuickActionDialog {
    private static final TypedKey<Dialog> KEY = DialogKeys.create(Key.key("mlc:quick_action_test"));

    private QuickActionDialog() {
    }

    public static void register(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(RegistryEvents.DIALOG.compose(), event ->
                event.registry().register(KEY, builder -> builder
                        .base(DialogBase.builder(Component.text("测试界面")).build())
                        .type(DialogType.notice())));
        context.getLifecycleManager().registerEventHandler(
                LifecycleEvents.TAGS.preFlatten(RegistryKey.DIALOG), event ->
                        event.registrar().addToTag(DialogTagKeys.QUICK_ACTIONS, List.of(TagEntry.valueEntry(KEY))));
    }
}
