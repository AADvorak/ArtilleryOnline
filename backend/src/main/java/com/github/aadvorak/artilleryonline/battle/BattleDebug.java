package com.github.aadvorak.artilleryonline.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleDebug {

    private boolean paused = false;

    private boolean doStep = false;

    private boolean forceSend = false;

    private String tracking;
}
