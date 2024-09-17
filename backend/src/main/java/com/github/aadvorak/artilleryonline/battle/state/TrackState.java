package com.github.aadvorak.artilleryonline.battle.state;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TrackState implements State {

    private boolean broken = false;

    private double repairRemainTime;
}
