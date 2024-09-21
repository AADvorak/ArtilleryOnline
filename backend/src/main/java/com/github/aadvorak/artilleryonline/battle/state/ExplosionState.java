package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ExplosionState implements State {

    private double time;

    private double radius;

    private Position position;
}
