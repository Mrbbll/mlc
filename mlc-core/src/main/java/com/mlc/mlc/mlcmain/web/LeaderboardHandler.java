package com.mlc.mlc.mlcmain.web;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.mlc.mlc.mlcmain.web.source.TotalAssaultSource;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 负责 v1 排行榜的参数校验、响应格式和缓存头。 */
final class LeaderboardHandler<T> {
    private static final Gson GSON = new Gson();

    private record Response<T>(String updatedAt, long totalPlayers, List<T> players) {}
    private record ErrorResponse(ApiError error) {}
    private record ApiError(String code, String message) {}

    private final Supplier<LeaderboardSnapshot<T>> snapshots;
    private final TotalAssaultSource totalAssault;
    private final Logger logger;
    private final boolean totalAssaultLeaderboard;

    LeaderboardHandler(Supplier<LeaderboardSnapshot<T>> snapshots) {
        this.snapshots = snapshots;
        this.totalAssault = null;
        this.logger = null;
        this.totalAssaultLeaderboard = false;
    }

    LeaderboardHandler(TotalAssaultSource totalAssault, Logger logger) {
        this(totalAssault, logger, false);
    }

    LeaderboardHandler(TotalAssaultSource totalAssault, Logger logger, boolean totalAssaultLeaderboard) {
        this.snapshots = null;
        this.totalAssault = totalAssault;
        this.logger = logger;
        this.totalAssaultLeaderboard = totalAssaultLeaderboard;
    }

    void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "GET");
            error(exchange, 405, "METHOD_NOT_ALLOWED", "Only GET is supported");
            return;
        }

        if (totalAssault != null) {
            if (totalAssaultLeaderboard) {
                try {
                    exchange.getResponseHeaders().set("Cache-Control", "no-store");
                    json(exchange, 200, totalAssault.leaderboard());
                } catch (SQLException | IllegalStateException exception) {
                    logger.log(Level.WARNING, "查询总力战排行榜失败。", exception);
                    error(exchange, 503, "UNAVAILABLE", "Total assault data is unavailable");
                }
                return;
            }
            handleTotalAssault(exchange);
            return;
        }

        Map<String, List<String>> query = queryParameters(exchange.getRequestURI().getRawQuery());
        List<String> limits = query.getOrDefault("limit", List.of("100"));
        int limit;
        try {
            limit = limits.size() == 1 ? Integer.parseInt(limits.getFirst()) : 0;
        } catch (NumberFormatException exception) {
            limit = 0;
        }
        if (limit < 1 || limit > LeaderboardSnapshot.MAX_LIMIT) {
            error(exchange, 400, "INVALID_LIMIT", "limit must be between 1 and 500");
            return;
        }
        if (!query.getOrDefault("period", List.of("all")).equals(List.of("all"))) {
            error(exchange, 400, "UNSUPPORTED_PERIOD", "period must be all");
            return;
        }

        LeaderboardSnapshot<T> snapshot = snapshots.get();
        if (snapshot == null) {
            error(exchange, 500, "INTERNAL_ERROR", "Leaderboard data is unavailable");
            return;
        }
        List<T> players = snapshot.players();
        long age = Math.max(0, Duration.between(snapshot.updatedAt(), Instant.now()).toSeconds());
        exchange.getResponseHeaders().set("Cache-Control", "public, max-age=300");
        exchange.getResponseHeaders().set("Age", Long.toString(age));
        json(exchange, 200, new Response<>(snapshot.updatedAt().toString(), snapshot.totalPlayers(),
                players.subList(0, Math.min(limit, players.size()))));
    }

    /** 历史成绩实时分页读取，外层字段与现有排行榜保持一致。 */
    private void handleTotalAssault(HttpExchange exchange) throws IOException {
        UUID player;
        String activity;
        int limit;
        int offset;
        try {
            Map<String, String> query = totalAssaultParameters(exchange.getRequestURI().getRawQuery());
            String playerValue = query.get("player");
            player = playerValue == null ? null : UUID.fromString(playerValue);
            activity = query.get("activity");
            if (activity != null && (activity.isBlank() || activity.length() > 128)) throw new IllegalArgumentException();
            limit = Integer.parseInt(query.getOrDefault("limit", "20"));
            offset = Integer.parseInt(query.getOrDefault("offset", "0"));
            if (limit < 1 || limit > 100 || offset < 0) throw new IllegalArgumentException();
        } catch (IllegalArgumentException exception) {
            error(exchange, 400, "INVALID_PARAMETERS", "Invalid player, activity, limit or offset");
            return;
        }
        try {
            var result = totalAssault.query(player, activity, limit, offset);
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            json(exchange, 200, new Response<>(Instant.now().toString(), result.total(), result.scores()));
        } catch (SQLException | IllegalStateException exception) {
            logger.log(Level.WARNING, "查询总力战历史成绩失败。", exception);
            error(exchange, 503, "UNAVAILABLE", "Total assault data is unavailable");
        }
    }

    /** 校验历史成绩筛选参数，防止重复参数产生歧义。 */
    private static Map<String, String> totalAssaultParameters(String raw) {
        Map<String, String> result = new HashMap<>();
        if (raw == null || raw.isEmpty()) return result;
        for (String pair : raw.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            if (!key.equals("player") && !key.equals("activity") && !key.equals("limit") && !key.equals("offset")) {
                throw new IllegalArgumentException();
            }
            String value = parts.length == 2 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
            if (result.putIfAbsent(key, value) != null) throw new IllegalArgumentException();
        }
        return result;
    }

    private static Map<String, List<String>> queryParameters(String query) {
        Map<String, List<String>> parameters = new HashMap<>();
        if (query != null) {
            for (String pair : query.split("&")) {
                String[] parts = pair.split("=", 2);
                String name = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                if ("limit".equals(name) || "period".equals(name)) {
                    String value = parts.length == 2 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
                    parameters.computeIfAbsent(name, key -> new ArrayList<>()).add(value);
                }
            }
        }
        return parameters;
    }

    static void error(HttpExchange exchange, int status, String code, String message) throws IOException {
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        json(exchange, status, new ErrorResponse(new ApiError(code, message)));
    }

    private static void json(HttpExchange exchange, int status, Object body) throws IOException {
        byte[] bytes = GSON.toJson(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        if ("HEAD".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(status, -1);
        } else {
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
        }
    }
}
