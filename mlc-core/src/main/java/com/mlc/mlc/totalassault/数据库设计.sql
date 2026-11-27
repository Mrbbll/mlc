-- 总力战 SQLite 结构草案；插件实际加载 src/main/resources/totalassault/schema.sql。
PRAGMA foreign_keys = ON;

CREATE TABLE ta_activity (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    boss_id TEXT NOT NULL,
    start_ms INTEGER NOT NULL,
    end_ms INTEGER NOT NULL CHECK (end_ms > start_ms)
);

CREATE TABLE ta_difficulty (
    activity_id TEXT NOT NULL REFERENCES ta_activity(id),
    difficulty_id TEXT NOT NULL,
    difficulty_order INTEGER NOT NULL CHECK (difficulty_order >= 0),
    PRIMARY KEY (activity_id, difficulty_id),
    UNIQUE (activity_id, difficulty_order),
    UNIQUE (activity_id, difficulty_id, difficulty_order)
);

-- 同一玩家每期每天一行，ticket_day 是北京时间 04:00 对应的日期。
CREATE TABLE ta_ticket_day (
    activity_id TEXT NOT NULL REFERENCES ta_activity(id),
    player_uuid TEXT NOT NULL,
    ticket_day TEXT NOT NULL,
    remaining INTEGER NOT NULL CHECK (remaining BETWEEN 0 AND 3),
    PRIMARY KEY (activity_id, player_uuid, ticket_day)
);

-- 创建世界前登记，崩溃后可找到未扣票、未完成创建的世界。
CREATE TABLE ta_world_preparation (
    player_uuid TEXT PRIMARY KEY,
    world_name TEXT NOT NULL UNIQUE,
    created_at_ms INTEGER NOT NULL,
    state TEXT NOT NULL CHECK (state IN ('CREATING', 'CLEANING', 'ERROR'))
);

CREATE TABLE ta_challenge (
    id TEXT PRIMARY KEY,
    activity_id TEXT NOT NULL,
    player_uuid TEXT NOT NULL,
    ticket_day TEXT NOT NULL,
    difficulty_id TEXT NOT NULL,
    difficulty_order INTEGER NOT NULL,
    status TEXT NOT NULL CHECK (status IN ('PREPARING', 'BATTLING', 'SUCCESS', 'FAILED')),
    end_reason TEXT CHECK (end_reason IN ('BOSS_DEFEATED', 'GIVE_UP', 'EXPIRED', 'ACTIVITY_ENDED')),
    started_at_ms INTEGER NOT NULL,
    expires_at_ms INTEGER NOT NULL CHECK (expires_at_ms > started_at_ms),
    ended_at_ms INTEGER,
    saved_boss_health REAL NOT NULL CHECK (saved_boss_health >= 0),
    saved_battle_ms INTEGER NOT NULL DEFAULT 0 CHECK (saved_battle_ms >= 0),
    world_name TEXT NOT NULL UNIQUE,
    world_state TEXT NOT NULL CHECK (world_state IN ('READY', 'CLEANING', 'DELETED', 'ERROR')),
    FOREIGN KEY (activity_id, difficulty_id, difficulty_order)
        REFERENCES ta_difficulty(activity_id, difficulty_id, difficulty_order),
    FOREIGN KEY (activity_id, player_uuid, ticket_day)
        REFERENCES ta_ticket_day(activity_id, player_uuid, ticket_day),
    CHECK ((status IN ('PREPARING', 'BATTLING') AND ended_at_ms IS NULL AND end_reason IS NULL)
        OR (status = 'SUCCESS' AND ended_at_ms IS NOT NULL AND end_reason = 'BOSS_DEFEATED')
        OR (status = 'FAILED' AND ended_at_ms IS NOT NULL AND end_reason IN ('GIVE_UP', 'EXPIRED', 'ACTIVITY_ENDED')))
);
CREATE UNIQUE INDEX ta_one_active_challenge ON ta_challenge(player_uuid)
    WHERE status IN ('PREPARING', 'BATTLING');

-- 每次进入战斗先写 ACTIVE；只有 SAVED 的出战参与装备禁用与成绩。
CREATE TABLE ta_attempt (
    id TEXT PRIMARY KEY,
    challenge_id TEXT NOT NULL REFERENCES ta_challenge(id),
    attempt_no INTEGER NOT NULL CHECK (attempt_no > 0),
    status TEXT NOT NULL CHECK (status IN ('ACTIVE', 'SAVED', 'ROLLED_BACK')),
    started_at_ms INTEGER NOT NULL,
    ended_at_ms INTEGER,
    battle_ms INTEGER CHECK (battle_ms >= 0),
    boss_health_after REAL CHECK (boss_health_after >= 0),
    UNIQUE (challenge_id, attempt_no),
    CHECK ((status = 'ACTIVE' AND ended_at_ms IS NULL AND battle_ms IS NULL AND boss_health_after IS NULL)
        OR (status = 'SAVED' AND ended_at_ms IS NOT NULL AND battle_ms IS NOT NULL AND boss_health_after IS NOT NULL)
        OR (status = 'ROLLED_BACK' AND ended_at_ms IS NOT NULL AND battle_ms IS NULL AND boss_health_after IS NULL))
);
CREATE UNIQUE INDEX ta_one_active_attempt ON ta_attempt(challenge_id) WHERE status = 'ACTIVE';

-- 每个非空进场槽位一行，完整物品快照包含数量、组件和耐久。
CREATE TABLE ta_attempt_item (
    attempt_id TEXT NOT NULL REFERENCES ta_attempt(id) ON DELETE CASCADE,
    slot TEXT NOT NULL CHECK (slot IN ('HEAD', 'CHEST', 'LEGS', 'FEET', 'MAIN_HAND', 'OFF_HAND', 'HOTBAR_2', 'HOTBAR_3')),
    identity_type TEXT NOT NULL CHECK (identity_type IN ('MODEL', 'MATERIAL')),
    identity_value TEXT NOT NULL,
    item_snapshot BLOB NOT NULL,
    PRIMARY KEY (attempt_id, slot)
);
CREATE INDEX ta_item_identity ON ta_attempt_item(identity_type, identity_value);

-- 只保存每期最佳成绩；指向成功挑战及其 SAVED 出战快照。
CREATE TABLE ta_best_score (
    activity_id TEXT NOT NULL REFERENCES ta_activity(id),
    player_uuid TEXT NOT NULL,
    challenge_id TEXT NOT NULL UNIQUE REFERENCES ta_challenge(id),
    difficulty_order INTEGER NOT NULL CHECK (difficulty_order >= 0),
    battle_ms INTEGER NOT NULL CHECK (battle_ms >= 0),
    achieved_at_ms INTEGER NOT NULL,
    PRIMARY KEY (activity_id, player_uuid)
);

-- 每期已通关最高难度；每次成功后单调递增。
CREATE TABLE ta_progress (
    activity_id TEXT NOT NULL REFERENCES ta_activity(id),
    player_uuid TEXT NOT NULL,
    highest_cleared_order INTEGER NOT NULL CHECK (highest_cleared_order >= 0),
    PRIMARY KEY (activity_id, player_uuid)
);

-- 外部邮件投递的待办；唯一业务键用于幂等投递。
CREATE TABLE ta_reward_delivery (
    reward_key TEXT PRIMARY KEY,
    activity_id TEXT NOT NULL REFERENCES ta_activity(id),
    player_uuid TEXT NOT NULL,
    challenge_id TEXT REFERENCES ta_challenge(id),
    reward_kind TEXT NOT NULL CHECK (reward_kind IN ('CLEAR', 'RANK')),
    payload BLOB NOT NULL,
    status TEXT NOT NULL CHECK (status IN ('PENDING', 'DELIVERED')),
    created_at_ms INTEGER NOT NULL,
    delivered_at_ms INTEGER,
    CHECK ((status = 'PENDING' AND delivered_at_ms IS NULL)
        OR (status = 'DELIVERED' AND delivered_at_ms IS NOT NULL))
);

PRAGMA user_version = 1;
