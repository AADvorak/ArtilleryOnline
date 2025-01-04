package com.github.aadvorak.artilleryonline.battle.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CollisionMode {
    ELASTICITY("elasticity"),
    IMPACT("impact");

    private final String name;

    public static CollisionMode getCollisionMode(String name) {
        if (ELASTICITY.getName().equals(name)) {
            return ELASTICITY;
        }
        return IMPACT;
    }
}
