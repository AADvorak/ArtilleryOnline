package com.github.aadvorak.artilleryonline.battle.common;

public class IdGenerator {

    private int id = 0;

    public int generate() {
        return ++id;
    }
}
