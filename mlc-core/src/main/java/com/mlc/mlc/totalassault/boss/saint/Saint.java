package com.mlc.mlc.totalassault.boss.saint;

import com.mlc.mlc.totalassault.boss.AssaultBoss;
import com.mlc.mlc.totalassault.boss.BossAttributes;
import com.mlc.mlc.totalassault.boss.BossHandle;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.tracker.EntityTracker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Mannequin;

import java.util.Objects;
import java.util.Set;

/** 圣徒定义；使用假人承载 BetterModel 模型，动画暂时占位。 */
public final class Saint implements AssaultBoss {
    private static final String MODEL_ID = "saint";
    /** 需要随血量一起保存的两条进度。 */
    public record Progress(double ultimate, double stun) {}
    @Override public String id() { return "saint"; }
    @Override public Set<String> bannedModels() { return Set.of(); }
    @Override public Set<Material> bannedMaterials() { return Set.of(); }

    /** 在准备挑战和扣票前确认模型已经加载。 */
    @Override
    public void validate() {
        if (!Bukkit.getPluginManager().isPluginEnabled("BetterModel")) {
            throw new IllegalStateException("BetterModel 未启用，无法使用圣徒模型");
        }
        if (BetterModel.model(MODEL_ID).isEmpty()) {
            throw new IllegalStateException("BetterModel 中未找到圣徒模型：" + MODEL_ID);
        }
    }

    @Override
    public BossAttributes attributes(String difficulty) {
        SaintDifficulty values = SaintDifficulty.of(difficulty);
        return new BossAttributes(values.health(), 0, 0);
    }

    @Override
    public BossHandle spawn(Location location, String difficulty, double health) {
        validate();
        BossAttributes attributes = attributes(difficulty);
        if (!Double.isFinite(health) || health <= 0 || health > attributes.health()) throw new IllegalArgumentException("圣徒血量无效");
        Mannequin entity = Objects.requireNonNull(location.getWorld()).spawn(location, Mannequin.class, mannequin -> {
            Objects.requireNonNull(mannequin.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(attributes.health());
            mannequin.setHealth(health);
            mannequin.setInvulnerable(false);
            mannequin.setImmovable(true);
            mannequin.setPersistent(true);
        });
        try {
            return new SaintSkills(entity, attributes.health(), new Model(entity), SaintDifficulty.of(difficulty));
        } catch (RuntimeException | LinkageError exception) {
            // 模型绑定失败时不要留下没有控制器的假人。
            entity.remove();
            throw exception;
        }
    }

    /** 绑定并清理 BetterModel 模型，动画名称尚未确定。 */
    static final class Model {
        private final EntityTracker tracker;

        Model(Mannequin entity) {
            tracker = BetterModel.model(MODEL_ID)
                    .orElseThrow(() -> new IllegalStateException("BetterModel 中未找到圣徒模型：" + MODEL_ID))
                    .getOrCreate(BukkitAdapter.adapt(entity));
        }

        void play(String skill) { }
        void close() { tracker.close(); }
    }

}
