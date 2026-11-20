package com.mlc.mlc.mlcmain.respacksender;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.mlc.mlc.Mlc.instance;

public final class packsender implements Listener {
    private static final URI UPLOAD_URL = URI.create("https://mcpacks.dev/api/v1/packs");
    private static final UUID PACK_ID = UUID.fromString("591ac508-1fe9-43a7-8a05-bb75afc8ce14");
    private static final long MAX_SIZE = 250L * 1024 * 1024;
    private static volatile ResourcePackInfo currentPack;
    private static boolean uploading;

    private packsender() {}

    public static void init() {
        Path directory = instance.getDataFolder().toPath().getParent().resolve("mlc/respack");
        try {
            Files.createDirectories(directory);
            YamlConfiguration cache = YamlConfiguration.loadConfiguration(cachePath().toFile());
            String url = cache.getString("url");
            String hash = cache.getString("sha1");
            if (url != null && hash != null) currentPack = packInfo(url, hash);
        } catch (IOException | IllegalArgumentException e) {
            instance.getLogger().log(Level.WARNING, "无法读取资源包缓存或创建 plugins/mlc/respack", e);
        }
        Bukkit.getPluginManager().registerEvents(new packsender(), instance);
        upload(null);
    }

    public static void upload(CommandSender sender) {
        if (uploading) {
            if (sender != null) sender.sendMessage(Component.text("资源包正在上传，请稍候。"));
            return;
        }
        uploading = true;
        if (sender != null) sender.sendMessage(Component.text("正在压缩并上传资源包……"));
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            try {
                Path directory = instance.getDataFolder().toPath().getParent().resolve("respack");
                UploadResult result = uploadPack(directory, UPLOAD_URL);
                ResourcePackInfo info = packInfo(result.url(), result.sha1());
                YamlConfiguration cache = new YamlConfiguration();
                cache.set("url", result.url());
                cache.set("sha1", result.sha1());
                try {
                    cache.save(cachePath().toFile());
                } catch (IOException e) {
                    instance.getLogger().log(Level.WARNING, "资源包上传成功，但无法保存缓存", e);
                }
                Bukkit.getScheduler().runTask(instance, () -> {
                    uploading = false;
                    currentPack = info;
                    instance.getLogger().info("资源包上传成功: " + result.url());
                    if (sender != null) sender.sendMessage(Component.text("资源包上传成功，正在向在线玩家分发。"));
                    Bukkit.getOnlinePlayers().forEach(packsender::send);
                });
            } catch (Exception e) {
                if (e instanceof InterruptedException) Thread.currentThread().interrupt();
                instance.getLogger().log(Level.WARNING, "资源包上传失败，保留原有资源包", e);
                if (instance.isEnabled()) Bukkit.getScheduler().runTask(instance, () -> {
                    uploading = false;
                    if (sender != null) sender.sendMessage(Component.text("资源包上传失败，请查看控制台；原有资源包保持不变。"));
                });
            }
        });
    }

    private static Path cachePath() {
        return instance.getDataFolder().toPath().resolve("resource-pack-cache.yml");
    }

    static UploadResult uploadPack(Path directory, URI endpoint) throws IOException, InterruptedException, NoSuchAlgorithmException {
        if (!Files.isRegularFile(directory.resolve("pack.mcmeta"), LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("请将 pack.mcmeta 放在 plugins/respack 目录根部");
        }
        Path zip = Files.createTempFile("mlc-resource-pack-", ".zip");
        try {
            try (OutputStream file = Files.newOutputStream(zip); ZipOutputStream output = new ZipOutputStream(file);
                 var paths = Files.walk(directory)) {
                for (Path path : paths.filter(p -> Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS)).sorted().toList()) {
                    ZipEntry entry = new ZipEntry(directory.relativize(path).toString().replace('\\', '/'));
                    output.putNextEntry(entry);
                    Files.copy(path, output);
                    output.closeEntry();
                }
            }
            if (Files.size(zip) > MAX_SIZE) throw new IOException("资源包 ZIP 超过 250 MiB");
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            try (var input = Files.newInputStream(zip)) {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = input.read(buffer)) != -1) digest.update(buffer, 0, count);
            }
            String localHash = HexFormat.of().formatHex(digest.digest());
            String boundary = "mlc-" + UUID.randomUUID();
            var prefix = HttpRequest.BodyPublishers.ofString("--" + boundary + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"resource-pack.zip\"\r\nContent-Type: application/zip\r\n\r\n", StandardCharsets.UTF_8);
            var suffix = HttpRequest.BodyPublishers.ofString("\r\n--" + boundary + "--\r\n", StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder(endpoint).timeout(Duration.ofMinutes(5))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.concat(prefix, HttpRequest.BodyPublishers.ofFile(zip), suffix)).build();
            HttpResponse<String> response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 201) throw new IOException("MCPacks 返回 HTTP " + response.statusCode() + ": " + response.body());
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            if (!json.has("success") || !json.get("success").getAsBoolean()) throw new IOException("MCPacks 上传失败: " + response.body());
            JsonObject data = json.getAsJsonObject("data");
            String hash = data.get("sha1").getAsString();
            if (!localHash.equalsIgnoreCase(hash)) throw new IOException("MCPacks 返回的 SHA-1 与上传文件不匹配");
            String url = data.get("download_url").getAsString();
            packInfo(url, hash);
            return new UploadResult(url, hash);
        } finally {
            Files.deleteIfExists(zip);
        }
    }

    private static ResourcePackInfo packInfo(String url, String hash) {
        URI uri = URI.create(url);
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null || !hash.matches("(?i)[0-9a-f]{40}")) {
            throw new IllegalArgumentException("资源包 URL 或 SHA-1 无效");
        }
        return ResourcePackInfo.resourcePackInfo().id(PACK_ID).uri(uri).hash(hash).build();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        send(event.getPlayer());
    }

    private static void send(Player player) {
        ResourcePackInfo info = currentPack;
        if (info == null) return;
        player.sendResourcePacks(ResourcePackRequest.resourcePackRequest().packs(info)
                .prompt(Component.text("请下载服务器材质包")) .required(true).build());
    }

    record UploadResult(String url, String sha1) {}
}
