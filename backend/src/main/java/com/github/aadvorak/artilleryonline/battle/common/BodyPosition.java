package com.github.aadvorak.artilleryonline.battle.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BodyPosition implements BodyVector {

    private double x;

    private double y;

    private double angle;

    public BodyPosition setAngle(double angle) {
        if (angle < -Math.PI || angle > Math.PI) {
            this.angle = Math.atan2(Math.sin(angle), Math.cos(angle));
        } else {
            this.angle = angle;
        }
        return this;
    }

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

    public BodyPosition shifted(Shift shift) {
        return new BodyPosition()
                .setX(x + shift.distance() * Math.cos(shift.angle()))
                .setY(y + shift.distance() * Math.sin(shift.angle()))
                .setAngle(angle);
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
