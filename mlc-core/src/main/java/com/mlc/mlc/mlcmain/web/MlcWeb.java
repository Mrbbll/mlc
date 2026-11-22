package com.mlc.mlc.mlcmain.web;

import com.mlc.mlc.mlcmain.web.source.EconomyLeaderboardSource;
import com.mlc.mlc.mlcmain.web.source.PapiPlaytimeSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.logging.Level;

/** Web 功能入口，由 MLC 在启用和停用时管理，服务配置在重启后生效。 */
public final class MlcWeb implements AutoCloseable {
    private final PapiPlaytimeSource playtime;
    private final EconomyLeaderboardSource economy;
    private final WebServer server;

    private MlcWeb(PapiPlaytimeSource playtime, EconomyLeaderboardSource economy, WebServer server) {
        this.playtime = playtime;
        this.economy = economy;
        this.server = server;
    }

    public static MlcWeb start(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean("web.enabled", true)) {
            return null;
        }
        MlcWeb web = null;
        try {
            String host = config.getString("web.bind-address", "127.0.0.1");
            int port = config.getInt("web.port", 8090);
            if (host.isBlank() || port < 1 || port > 65535) {
                throw new IllegalArgumentException("web.bind-address 或 web.port 无效");
            }
            PapiPlaytimeSource playtime = new PapiPlaytimeSource(plugin);
            EconomyLeaderboardSource economy = new EconomyLeaderboardSource(plugin.getLogger());
            WebServer server = new WebServer(new InetSocketAddress(host, port), Map.of(
                    "/api/v1/leaderboard/playtime", new LeaderboardHandler<>(playtime::latest),
                    "/api/v1/leaderboard/economy", new LeaderboardHandler<>(economy::latest)
            ), plugin.getLogger());
            web = new MlcWeb(playtime, economy, server);
            server.start();
            playtime.start();
            economy.start();
            plugin.getLogger().info("MLC Web 已监听 " + server.address());
            return web;
        } catch (IOException | RuntimeException exception) {
            if (web != null) {
                web.close();
            }
            plugin.getLogger().log(Level.WARNING, "MLC Web 启动失败。", exception);
            return null;
        }
    }

    @Override
    public void close() {
        server.close();
        economy.close();
        playtime.close();
    }
}
