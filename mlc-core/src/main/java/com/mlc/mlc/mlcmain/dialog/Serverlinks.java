package com.mlc.mlc.mlcmain.dialog;

import net.kyori.adventure.text.Component;

import java.net.URI;
import java.net.URISyntaxException;

import static com.mlc.mlc.Mlc.instance;

public class Serverlinks {
    public static void setserverLinks() throws URISyntaxException {
        instance.getServer().getServerLinks().addLink(Component.text("服务器在线地图"),new URI("http://43.248.188.28:19423/"));

    }

}
