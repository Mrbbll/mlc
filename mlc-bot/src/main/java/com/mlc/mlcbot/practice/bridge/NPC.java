package com.mlc.mlcbot.practice.bridge;
import com.mlc.mlcbot.BotSession;
import org.bukkit.entity.Player;
public final class NPC {
    private final BotSession session;
    private final Navigator navigator;
    private final FollowTrait follow;
    public NPC(BotSession session){this.session=session;navigator=new Navigator(session);follow=new FollowTrait(navigator);CitizensAPI.getNPCRegistry().register(this);}
    public Player getEntity(){return session.handle.getBukkitEntity();}
    public int getId(){return session.handle.getId();}
    public Navigator getNavigator(){return navigator;}
    public void tick(long tick){follow.tick();navigator.tick(tick);}
    public <T> T getTraitNullable(Class<T> type){return type==FollowTrait.class?type.cast(follow):null;}
    public <T> T getOrAddTrait(Class<T> type){return getTraitNullable(type);}
    public void close(){navigator.cancelNavigation();CitizensAPI.getNPCRegistry().remove(this);}
}
