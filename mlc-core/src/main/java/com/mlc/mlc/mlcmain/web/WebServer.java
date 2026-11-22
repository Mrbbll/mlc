package com.mlc.mlc.mlcmain.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 提供 HTTP API 路由，关闭时释放监听器和工作线程。 */
final class WebServer implements AutoCloseable {
    private final HttpServer server;
    private final ThreadPoolExecutor workers;

    WebServer(InetSocketAddress address, Map<String, LeaderboardHandler<?>> routes, Logger logger)
            throws IOException {
        server = HttpServer.create(address, 32);
        workers = new ThreadPoolExecutor(2, 8, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(64),
                Thread.ofPlatform().daemon().name("mlc-web-", 0).factory());
        server.setExecutor(workers);
        server.createContext("/", exchange -> dispatch(exchange, routes, logger));
    }

    void start() {
        server.start();
    }

    InetSocketAddress address() {
        return server.getAddress();
    }

    private static void dispatch(HttpExchange exchange, Map<String, LeaderboardHandler<?>> routes, Logger logger)
            throws IOException {
        try (exchange) {
            try {
                exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
                LeaderboardHandler<?> leaderboard = routes.get(exchange.getRequestURI().getPath());
                if (leaderboard != null) {
                    leaderboard.handle(exchange);
                } else {
                    LeaderboardHandler.error(exchange, 404, "NOT_FOUND", "Unknown API endpoint");
                }
            } catch (IOException | RuntimeException exception) {
                if (exchange.getResponseCode() != -1) {
                    // 响应已经开始，例如浏览器中途断开，无法再发送错误响应。
                    logger.log(Level.FINE, "MLC Web 响应中断。", exception);
                } else {
                    logger.log(Level.WARNING, "MLC Web 请求处理失败。", exception);
                    LeaderboardHandler.error(exchange, 500, "INTERNAL_ERROR", "Internal server error");
                }
            }
        }
    }

    @Override
    public void close() {
        server.stop(0);
        workers.shutdownNow();
    }
}
