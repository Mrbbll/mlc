package com.mlc.mlc.totalassault.ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

/** 持久化每日票数；调用者负责在成功分配场地后扣票。 */
public final class ticketmanager {
    private static final ZoneId BEIJING = ZoneId.of("Asia/Shanghai");
    private final Connection connection;

    public ticketmanager(Connection connection) throws SQLException {
        this.connection = connection;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ta_tickets (player TEXT NOT NULL, activity TEXT NOT NULL, day TEXT NOT NULL, remaining INTEGER NOT NULL, PRIMARY KEY (player, activity))");
        }
    }

    public static boolean isActive(Instant now, Instant start) {
        return !now.isBefore(start) && now.isBefore(start.plusSeconds(7 * 24 * 60 * 60));
    }

    public static LocalDate ticketDay(Instant now) {
        return now.atZone(BEIJING).minusHours(4).toLocalDate();
    }

    public synchronized int giveticket(UUID player, String activity, Instant now) throws SQLException {
        refresh(player, activity, now);
        try (PreparedStatement query = connection.prepareStatement("SELECT remaining FROM ta_tickets WHERE player=? AND activity=?")) {
            query.setString(1, player.toString());
            query.setString(2, activity);
            try (ResultSet result = query.executeQuery()) {
                if (!result.next()) throw new SQLException("Ticket row missing after refresh");
                return result.getInt(1);
            }
        }
    }

    public synchronized boolean delticket(UUID player, String activity, Instant now) throws SQLException {
        refresh(player, activity, now);
        try (PreparedStatement update = connection.prepareStatement("UPDATE ta_tickets SET remaining=remaining-1 WHERE player=? AND activity=? AND remaining>0")) {
            update.setString(1, player.toString());
            update.setString(2, activity);
            return update.executeUpdate() == 1;
        }
    }

    private void refresh(UUID player, String activity, Instant now) throws SQLException {
        try (PreparedStatement update = connection.prepareStatement(
                "INSERT INTO ta_tickets(player, activity, day, remaining) VALUES(?,?,?,3) "
                        + "ON CONFLICT(player, activity) DO UPDATE SET day=excluded.day, remaining=3 WHERE day<>excluded.day")) {
            update.setString(1, player.toString());
            update.setString(2, activity);
            update.setString(3, ticketDay(now).toString());
            update.executeUpdate();
        }
    }
}
