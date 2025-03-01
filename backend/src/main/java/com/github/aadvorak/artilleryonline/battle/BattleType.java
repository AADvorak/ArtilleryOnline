package com.github.aadvorak.artilleryonline.battle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BattleType {
    TEST_DRIVE((short) 1),
    RANDOM((short) 2),
    ROOM((short) 3),
    DRONE_HUNT((short) 4),;

    private final Short id;
}
