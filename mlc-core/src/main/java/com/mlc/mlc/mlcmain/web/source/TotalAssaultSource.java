package com.mlc.mlc.mlcmain.web.source;

import com.mlc.mlc.totalassault.data.Datamanager;
import com.mlc.mlc.totalassault.data.TotalAssaultStorage;

import java.sql.SQLException;
import java.util.UUID;

/** 查询历期总力战最佳成绩和对应的出战物品快照。 */
public final class TotalAssaultSource {
    public TotalAssaultStorage.HistoryPage query(UUID player, String activity, int limit, int offset)
            throws SQLException {
        return Datamanager.scoreHistory(player, activity, limit, offset);
    }
}
