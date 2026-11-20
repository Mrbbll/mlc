package com.mlc.mlc.mlcmain.damageindicator;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class DamageIndicatorService implements Listener {
    private final JavaPlugin plugin;
    private final DamageIndicatorConfig config;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final DecimalFormat damageFormat = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.ROOT));
    private final Set<DamageHologram> activeHolograms = new HashSet<>();

    public DamageIndicatorService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = new DamageIndicatorConfig(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!config.enabled() || !(event.getEntity() instanceof LivingEntity victim)) return;

        // Capture the position before a lethal hit can remove the victim.
        Location location = createLocation(victim);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (config.enabled() && !event.isCancelled()) {
                show(location, event.getFinalDamage());
            }
        });
    }

    public void reload() {
        config.reload();
    }

    public void shutdown() {
        activeHolograms.forEach(DamageHologram::destroy);
        activeHolograms.clear();
    }

    private void show(Location location, double finalDamage) {
        double viewDistance = config.viewDistance();
        double viewDistanceSquared = viewDistance * viewDistance;
        List<Player> viewers = location.getWorld().getPlayers().stream()
                .filter(player -> player.getLocation().distanceSquared(location) <= viewDistanceSquared)
                .toList();
        if (viewers.isEmpty()) return;

        String format = finalDamage <= 0.0d
                ? config.missFormat()
                : config.damageFormat().replace("<damage>", damageFormat.format(finalDamage));
        DamageHologram hologram = new DamageHologram(plugin, config, location, miniMessage.deserialize(format), viewers);
        activeHolograms.add(hologram);
        hologram.start(() -> {
            if (activeHolograms.remove(hologram)) {
                hologram.destroy();
            }
        });
    }

    private Location createLocation(LivingEntity victim) {
        double spread = config.horizontalSpread();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double x = spread > 0.0d ? random.nextDouble(-spread, spread) : 0.0d;
        double z = spread > 0.0d ? random.nextDouble(-spread, spread) : 0.0d;
        return victim.getLocation().add(x, victim.getHeight() + config.heightOffset(), z);
    }
}
