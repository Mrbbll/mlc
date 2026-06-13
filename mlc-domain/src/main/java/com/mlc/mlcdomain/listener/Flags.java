package com.mlc.mlcdomain.listener;

public enum Flags {
    PLAYER_INTERACT("player_interact"),
    BLOCK_PLACE("block_place"),
    BLOCK_BREAK("block_break");


    private final String flag;
    Flags(String flag) {
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }
}
