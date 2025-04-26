package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.BodyVelocity;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;

public interface BodyState extends State {

    BodyPosition getPosition();

    BodyVelocity getVelocity();

    MovingDirection getPushingDirection();

    MovingDirection getRotatingDirection();
}
