package com.mlc.mlc.totalassault.boss.saint;

import com.mlc.mlc.totalassault.boss.AssaultBoss;
import com.mlc.mlc.totalassault.boss.BossAttributes;
import com.mlc.mlc.totalassault.boss.BossHandle;
import com.mlc.mlc.totalassault.TaText;
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
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

/** 圣徒定义；使用假人承载 BetterModel 模型。 */
public final class Saint implements AssaultBoss {
    private static final String MODEL_ID = "saint";
    /** 需要随血量一起保存的两条进度。 */
    public record Progress(double ultimate, double stun) {}
    @Override public String id() { return "saint"; }
    @Override public String displayName() { return "圣徒"; }
    @Override public Set<String> bannedModels() { return Set.of(); }
    @Override public Set<Material> bannedMaterials() { return Set.of(); }

    /** 根据实际启用的技能和本难度参数生成菜单说明。 */
    @Override
    public List<SkillInfo> skillInfo(String difficulty) {
        SaintDifficulty values = SaintDifficulty.of(difficulty);
        List<SkillInfo> result = new ArrayList<>();
        for (SaintDifficulty.Skill skill : SaintDifficulty.Skill.values()) {
            if (!values.hasSkill(skill)) continue;
            List<String> details = new ArrayList<>();
            var settings = values.skills().get(skill);
            if (settings instanceof SaintDifficulty.AreaSkill area) {
                details.add("伤害：" + area.damage() + "　范围：" + area.radius() + " 格");
                details.add("预警：" + area.warningTicks() + " 刻　攻击次数：" + area.strikes());
                if (area.slownessTicks() > 0) details.add("迟缓：" + (area.slownessAmplifier() + 1) + " 级，持续 " + area.slownessTicks() + " 刻");
            } else if (settings instanceof SaintDifficulty.ProjectileSkill projectile) {
                details.add("每发伤害：" + projectile.damage() + "　发射间隔：" + projectile.intervalTicks() + " 刻");
                if (projectile.freezeTicks() > 0) details.add("霜冻：" + projectile.freezeTicks() + " 刻");
            }
            result.add(new SkillInfo(skill.displayName(), skill.description(), List.copyOf(details)));
        }
        return List.copyOf(result);
    }

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
            // 使用总力战 Boss 名称，避免死亡消息显示原版假人名称。
            mannequin.customName(TaText.line("<#ffd166><b>圣徒"));
            mannequin.setCustomNameVisible(true);
        });
        try {
            return new SaintSkills(entity, attributes.health(), new Model(entity), SaintDifficulty.of(difficulty));
        } catch (RuntimeException | LinkageError exception) {
            // 模型绑定失败时不要留下没有控制器的假人。
            entity.remove();
            throw exception;
        }
    }

    /** 绑定 BetterModel 模型，并管理当前技能动画。 */
    static final class Model {
        private final EntityTracker tracker;
        private final Set<String> reportedFailures = new HashSet<>();
        private String currentAnimation;

        Model(Mannequin entity) {
            tracker = BetterModel.model(MODEL_ID)
                    .orElseThrow(() -> new IllegalStateException("BetterModel 中未找到圣徒模型：" + MODEL_ID))
                    .getOrCreate(BukkitAdapter.adapt(entity));
        }

        /** 新技能会替换上一技能的动画，避免循环动画在技能结束后继续播放。 */
        void play(String animation) {
            stop();
            boolean played = tracker.animate(animation);
            if (played) {
                currentAnimation = animation;
            } else if (reportedFailures.add(animation)) {
                Bukkit.getLogger().warning("圣徒动画无法播放，请检查 BetterModel 模型中的动画名称：" + animation);
            }
        }

        void stop() {
            if (currentAnimation == null) return;
            tracker.stopAnimation(currentAnimation);
            currentAnimation = null;
        }

        void close() { stop(); tracker.close(); }
    }

}
