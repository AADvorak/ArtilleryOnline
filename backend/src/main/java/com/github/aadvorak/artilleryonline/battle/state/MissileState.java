package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.battle.common.BodyAcceleration;
import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.BodyVelocity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MissileState implements State {

    private BodyPosition position = new BodyPosition();

    private BodyVelocity velocity = new BodyVelocity();

    private BodyAcceleration acceleration = new BodyAcceleration();
}
