package com.github.aadvorak.artilleryonline.battle.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BodyPosition {

    private double x;

    private double y;

    private double angle;

    @JsonIgnore
    public Position getCenter() {
        return new Position()
                .setX(x)
                .setY(y);
    }

    @JsonIgnore
    public BodyPosition next(BodyVelocity velocity, double timeStep) {
        return new BodyPosition()
                .setX(x + velocity.getX() * timeStep)
                .setY(y + velocity.getY() * timeStep)
                .setAngle(angle + velocity.getAngle() * timeStep);
    }

    @Override
    public String toString() {
        return String.format("(%.3f, %.3f, %.3f)", x, y, angle);
    }

    public static BodyPosition of(Position center, double angle) {
        return new BodyPosition()
                .setX(center.getX())
                .setY(center.getY())
                .setAngle(angle);
    }
}
