package com.mlc.mlc.mlcmain.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.List;

import static com.mlc.mlc.Mlc.fileConfiguration;
import static com.mlc.mlc.Mlc.miniMessage;

public class Serverjoindialog {
    public static Component gonggao;
    public static Dialog serverjoindialog;
    public static void initserverjoindialog(){
        gonggao = miniMessage.deserialize(fileConfiguration.getString("gonggao",""));
       serverjoindialog = Dialog.create(builder ->{
           builder.empty().base(DialogBase.builder(Component.text("MLC公告", NamedTextColor.LIGHT_PURPLE))
                           .canCloseWithEscape(false)
                           .body(List.of(
                                   DialogBody.plainMessage(gonggao)
                           ))
                           .build()
                   )
                   .type(DialogType.confirmation(
                           ActionButton.builder(Component.text("好的喵!", TextColor.color(0xEDC7FF)))
                                   .tooltip(Component.text("欢迎喵!"))
                                   .action(DialogAction.customClick(Key.key("mlc:gonggao/agree"), null))
                                   .build(),
                           ActionButton.builder(Component.text("暂时还不想进...", TextColor.color(0xFF8B8E)))
                                   .tooltip(Component.text("点了就退出喵..."))
                                   .action(DialogAction.customClick(Key.key("mlc:gonggao/disagree"), null))
                                   .build()
                   ));
            }
       );
    }
}
