package com.mlc.mlcbot.practice.bridge;
import org.bukkit.entity.Entity;
public final class FollowTrait {
    private Entity following;
    private final Navigator navigator;
    FollowTrait(Navigator navigator){this.navigator=navigator;}
    public Entity getFollowing(){return following;}
    public void follow(Entity entity){following=entity;if(entity==null)navigator.cancelNavigation();else navigator.setTarget(entity,false);}
    public void setFollowingMargin(double margin){navigator.getDefaultParameters().distanceMargin(margin);}
    /** Same retarget behavior as Citizens FollowTrait.run(), including recovery after navigation cancellation. */
    public void tick(){if(following!=null&&following.isValid()&&!navigator.isNavigating())navigator.setTarget(following,false);}
}
