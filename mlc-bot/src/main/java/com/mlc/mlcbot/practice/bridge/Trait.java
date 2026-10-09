package com.mlc.mlcbot.practice.bridge;
public abstract class Trait {
    protected NPC npc;
    protected Trait(String name){}
    public void attach(NPC npc){this.npc=npc;onAttach();}
    public void onAttach(){}
    public void onSpawn(){}
}
