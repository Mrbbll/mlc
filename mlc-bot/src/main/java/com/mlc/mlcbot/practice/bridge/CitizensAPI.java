package com.mlc.mlcbot.practice.bridge;
import com.mlc.mlcbot.BotIdentity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Entity;
/** Runtime lookup for the embedded Citizens-derived NPC implementation. */
public final class CitizensAPI {
    private static final Registry REGISTRY=new Registry();
    public static Registry getNPCRegistry(){return REGISTRY;}
    public static final class Registry {
        private final Map<UUID,NPC> npcs=new HashMap<>();
        public NPC getNPC(Entity entity){return entity==null?null:npcs.get(entity.getUniqueId());}
        // The metadata fallback is only for NPC markers owned by external plugins.
        public boolean isNPC(Entity entity){return entity!=null&&(getNPC(entity)!=null||BotIdentity.isBot(entity)||entity.hasMetadata("NPC"));}
        public void register(NPC npc){npcs.put(npc.getEntity().getUniqueId(),npc);}
        public void remove(NPC npc){npcs.remove(npc.getEntity().getUniqueId());}
        public int size(){return npcs.size();}
    }
}
