package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.battle.common.*;

public interface BodyState extends State {

    BodyPosition getPosition();

    BodyVelocity getVelocity();

    MovingDirection getPushingDirection();

    void setPushingDirection(MovingDirection pushingDirection);

    MovingDirection getRotatingDirection();

    void setRotatingDirection(MovingDirection rotatingDirection);

    default Velocity getVelocityAt(Position position) {
        var rotatingVelocity = getRotatingVelocityAt(position);
        return new Velocity()
                .setX(getVelocity().getX() + rotatingVelocity.getX())
                .setY(getVelocity().getY() + rotatingVelocity.getY());
    }

    default Velocity getRotatingVelocityAt(Position position) {
        var bodyVelocity = getVelocity();
        var comPosition = getPosition().getCenter();
        var angle = comPosition.angleTo(position);
        var distance = comPosition.distanceTo(position);
        return new Velocity()
                .setX(-bodyVelocity.getAngle() * distance * Math.sin(angle))
                .setY(bodyVelocity.getAngle() * distance * Math.cos(angle));
    }

    default void applyNormalMoveToPosition(double normalMove, double angle) {
        var move = new VectorProjections(angle).setNormal(normalMove).recoverPosition();
        getPosition().setX(getPosition().getX() + move.getX());
        getPosition().setY(getPosition().getY() + move.getY());
    }
}
