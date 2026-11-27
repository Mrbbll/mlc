package com.mlc.mlc.totalassault.ticket;

import com.mlc.mlc.totalassault.data.Datamanager;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** 查询每日票数；扣票只能通过 startChallenge 与创建挑战一起提交。 */
public final class ticketmanager {
    public int giveticket(UUID player, String activity, Instant now) throws SQLException {
        return Datamanager.remainingTickets(player, activity, now);
    }
    public static LocalDate ticketDay(Instant now) { return Datamanager.ticketDay(now); }
    public static boolean isActive(Instant now, Instant start) {
        return !now.isBefore(start) && now.isBefore(start.plusSeconds(7L * 24 * 60 * 60));
    }
}
