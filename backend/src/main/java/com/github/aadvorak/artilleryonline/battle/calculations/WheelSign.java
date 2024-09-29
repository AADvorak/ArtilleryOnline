package com.github.aadvorak.artilleryonline.battle.calculations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WheelSign {
    RIGHT(-1),
    LEFT(1);

    private final int value;
}
