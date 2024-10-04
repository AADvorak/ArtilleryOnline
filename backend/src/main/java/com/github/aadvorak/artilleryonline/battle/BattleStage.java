package com.github.aadvorak.artilleryonline.battle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BattleStage {
    WAITING(5 * 1000),
    ACTIVE(5 * 60 * 1000),
    FINISHED(0);

    private final long maxTime;
}
