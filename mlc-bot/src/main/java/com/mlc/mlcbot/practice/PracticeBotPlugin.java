package com.mlc.mlcbot.practice;
import com.mlc.mlcbot.practice.x.b;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
/** Only the original combat context. No independent plugin, config output, UI or persistence. */
public final class PracticeBotPlugin {
    private static PracticeBotPlugin instance;
    private final JavaPlugin plugin;
    private final b config;
    private final CombatManager manager = new CombatManager();
    public PracticeBotPlugin(JavaPlugin plugin) {
        this.plugin=plugin;
        var stream=plugin.getResource("META-INF/mlc-bot/practicebot-defaults.yml");
        if(stream==null) throw new IllegalStateException("Missing built-in PracticeBot combat defaults");
        try(var reader=new InputStreamReader(stream,StandardCharsets.UTF_8)) {
            config=new b(YamlConfiguration.loadConfiguration(reader));
        } catch(java.io.IOException e) { throw new IllegalStateException(e); }
        instance=this;
    }
    public JavaPlugin plugin(){return plugin;}
    public static PracticeBotPlugin getInstance(){return instance;}
    public b getConfigManager(){return config;}
    public CombatManager getBotManager(){return manager;}
    public org.bukkit.configuration.file.FileConfiguration getDefaultInvConfig(){return null;}
    public void addCombatTag(org.bukkit.entity.Player player){}
    public boolean isShieldVisualSupported(){return true;}
    public java.lang.reflect.Method getStartUsingItemMethod(){
        try{return org.bukkit.entity.LivingEntity.class.getMethod("startUsingItem",org.bukkit.inventory.EquipmentSlot.class);}
        catch(NoSuchMethodException e){return null;}
    }
    public static void shutdown(){instance=null;}
    public static final class CombatManager {
        private final Kits kits=new Kits();
        public Kits H(){return kits;}
        public int L(){return com.mlc.mlcbot.practice.bridge.CitizensAPI.getNPCRegistry().size();}
    }
    public static final class Kits {
        public ItemStack E(){var sword=new ItemStack(Material.NETHERITE_SWORD);sword.addUnsafeEnchantment(Enchantment.SHARPNESS,5);sword.editMeta(meta->meta.setUnbreakable(true));return sword;}
        public Material g(String material,String part){var result=Material.matchMaterial(material.toUpperCase(java.util.Locale.ROOT)+"_"+part);return result==null?Material.valueOf("NETHERITE_"+part):result;}
    }
}
