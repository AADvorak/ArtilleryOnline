package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.model.BodyModel;

public interface BodyState extends State {

    BodyPosition getPosition();

    BodyVelocity getVelocity();

    MovingDirection getPushingDirection();

    MovingDirection getRotatingDirection();

    default Velocity getVelocityAt(Position position) {
        var bodyVelocity = getVelocity();
        var comPosition = getPosition().getCenter();
        var angle = comPosition.angleTo(position);
        var distance = comPosition.distanceTo(position);
        var rvx = bodyVelocity.getAngle() * distance * Math.sin(angle);
        var rvy = bodyVelocity.getAngle() * distance * Math.cos(angle);
        return new Velocity()
                .setX(bodyVelocity.getX() + rvx)
                .setY(bodyVelocity.getY() + rvy);
    }
}
