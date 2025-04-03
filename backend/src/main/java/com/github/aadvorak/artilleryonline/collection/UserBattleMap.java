package com.github.aadvorak.artilleryonline.collection;

import com.github.aadvorak.artilleryonline.battle.Battle;

import java.util.concurrent.ConcurrentHashMap;

public class UserBattleMap extends ConcurrentHashMap<Long, Battle> {

    public long battlesCount() {
        return values().stream().map(Battle::getId).distinct().count();
    }
}
