package com.mlc.mlcbot.practice.bridge;
import com.mlc.mlcbot.BotSession;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
/** Citizens follow/navigation surface backed by the module's incremental A* navigator. */
public final class Navigator {
    private final BotSession session;
    private final Parameters parameters=new Parameters();
    private Entity entity;
    private Location location;
    public Navigator(BotSession session){this.session=session;}
    public Parameters getDefaultParameters(){return parameters;}
    public Parameters getLocalParameters(){return parameters;}
    public void setTarget(Entity target,boolean aggressive){entity=target;location=null;}
    public void setTarget(Location target){entity=null;location=target;}
    public boolean isNavigating(){return entity!=null||location!=null;}
    public void cancelNavigation(){entity=null;location=null;session.navigator.stop();session.handle.steer(new Vector(),false);}
    public void tick(long tick){
        if(!isNavigating())return;
        var bot=session.handle.getBukkitEntity();var at=bot.getLocation();
        var target=entity==null?location:entity.getLocation();
        if(target==null||!at.getWorld().equals(target.getWorld())||(entity!=null&&!entity.isValid())){cancelNavigation();return;}
        if(at.distanceSquared(target)<=parameters.margin*parameters.margin){session.handle.steer(new Vector(),false);return;}
        Vector direction=session.navigator.direction(at,target,tick).multiply(Math.min(1.5,parameters.speed));
        if(session.type==com.mlc.mlcbot.BotType.NORMAL&&direction.lengthSquared()>.001){
            bot.setRotation((float)Math.toDegrees(Math.atan2(-direction.getX(),direction.getZ())),0);
        }
        session.handle.steer(direction,session.navigator.shouldJump(at)||bot.isInWater());
    }
    public static final class Parameters {
        private float speed=1;
        private double margin=.5;
        public Parameters speedModifier(float value){speed=value;return this;}
        public Parameters distanceMargin(double value){margin=value;return this;}
    }
}
