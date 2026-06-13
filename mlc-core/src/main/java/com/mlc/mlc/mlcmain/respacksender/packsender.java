package com.mlc.mlc.mlcmain.respacksender;

import io.papermc.paper.connection.PlayerConfigurationConnection;
import net.kyori.adventure.resource.ResourcePackCallback;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.mlc.mlc.Mlc.fileConfiguration;
import static com.mlc.mlc.Mlc.instance;

public class packsender {
    private static ResourcePackInfo resourcePackInfo;


    public static void init() throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        String input = fileConfiguration.getString("resourcepack");

        if (input != null) {
            // 计算远程资源包哈希
            URI packuri = URI.create(input);
            String hash = getremotehash(packuri);



            resourcePackInfo = ResourcePackInfo.resourcePackInfo()
                    .uri(packuri)
                    .hash(hash)
                    .build();
        }else {
            instance.getLogger().warning("资源包链接错误，服务器将关闭");
            instance.getServer().shutdown();
        }
    }

    private static String getremotehash(URI uri) throws IOException, NoSuchAlgorithmException {
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000); // 连接超时5秒
        conn.setReadTimeout(10000);
        //获取网络输入流
        try (InputStream is = conn.getInputStream()) {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[8192]; // 8KB缓冲区，高效读取
            int bytesRead;

            //分块读取流并更新哈希计算
            while ((bytesRead = is.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }

            // 生成十六进制哈希字符串
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } finally {
            conn.disconnect(); // 关闭连接
        }
    }

    public static void send(PlayerConfigurationConnection connection){

        //回调函数
        ResourcePackCallback cb = ResourcePackCallback.onTerminal(
                (uuid, audience) -> {
                    instance.getLogger().info("玩家 " + uuid + " 材质包下载成功");
                    audience.sendMessage(Component.text("材质包下载成功", NamedTextColor.GREEN));
                },
                (uuid, audience) -> {
                    instance.getLogger().info("玩家 " + uuid + " 材质包下载失败");
                    connection.disconnect(Component.text("材质包下载失败", NamedTextColor.RED));
                }
        );

        ResourcePackRequest resourcePackRequest = ResourcePackRequest.resourcePackRequest()
                .packs(resourcePackInfo)
                .prompt(Component.text("请下载服务器材质包"))
                .required(true)
//                .callback(cb)
                .build();
        connection.getAudience().sendResourcePacks(resourcePackRequest);


        ;
    }

}
