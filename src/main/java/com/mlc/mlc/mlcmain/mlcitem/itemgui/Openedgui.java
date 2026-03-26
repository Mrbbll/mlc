package com.mlc.mlc.mlcmain.mlcitem.itemgui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Openedgui {
    private Player owner;
    private int num;
    private Inventory inv;
    private Guitype guitype;
    public Openedgui(Player owner, int num, Inventory inv,Guitype guitype) {
        this.owner = owner;
        this.num = num;
        this.inv = inv;
        this.guitype = guitype;
    }
    public Player getOwner() {
        return owner;
    }
    public int getNum() {
        return num;
    }
    public Inventory getInv() {
        return inv;
    }
    public Guitype getGuitype() {
        return guitype;
    }
    public void setNum(int num) {
        this.num = num;
    }
    public void setInv(Inventory inv) {
        this.inv = inv;
    }
    public void setGuitype(Guitype guitype) {
        this.guitype = guitype;
    }

}
