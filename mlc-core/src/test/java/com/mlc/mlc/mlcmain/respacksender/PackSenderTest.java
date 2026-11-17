package com.mlc.mlc.mlcmain.respacksender;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

class PackSenderTest {
    @TempDir Path directory;

    @Test
    void uploadsMultipartWithPackFilesAtZipRoot() throws Exception {
        Files.writeString(directory.resolve("pack.mcmeta"), "{\"pack\":{}}", StandardCharsets.UTF_8);
        Files.createDirectories(directory.resolve("assets/example"));
        Files.writeString(directory.resolve("assets/example/test.txt"), "texture");
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/packs", exchange -> {
            try {
                String type = exchange.getRequestHeaders().getFirst("Content-Type");
                String boundary = type.substring(type.indexOf("boundary=") + 9);
                byte[] body = exchange.getRequestBody().readAllBytes();
                String content = new String(body, StandardCharsets.ISO_8859_1);
                int start = content.indexOf("\r\n\r\n") + 4;
                int end = content.lastIndexOf("\r\n--" + boundary + "--");
                assertTrue(content.substring(0, start).contains("name=\"file\""));
                byte[] zip = java.util.Arrays.copyOfRange(body, start, end);
                try (ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(zip))) {
                    assertEquals("assets/example/test.txt", in.getNextEntry().getName());
                    assertEquals("pack.mcmeta", in.getNextEntry().getName());
                    assertNull(in.getNextEntry());
                }
                String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1").digest(zip));
                byte[] response = ("{\"success\":true,\"data\":{\"download_url\":\"https://mcpacks.dev/pack/test/download\",\"sha1\":\"" + hash + "\"}}").getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(201, response.length);
                exchange.getResponseBody().write(response);
            } catch (Exception | AssertionError e) {
                exchange.sendResponseHeaders(500, -1);
            } finally {
                exchange.close();
            }
        });
        server.start();
        try {
            var result = packsender.uploadPack(directory, URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/packs"));
            assertEquals("https://mcpacks.dev/pack/test/download", result.url());
            assertEquals(40, result.sha1().length());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void rejectsMissingMetadataBeforeUpload() {
        assertThrows(java.io.IOException.class, () -> packsender.uploadPack(directory, URI.create("http://127.0.0.1:1/packs")));
    }
}
