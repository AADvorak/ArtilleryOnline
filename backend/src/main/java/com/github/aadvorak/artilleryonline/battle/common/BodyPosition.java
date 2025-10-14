package com.github.aadvorak.artilleryonline.battle.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

@Getter
public class BodyPosition extends BodyVectorBase implements BodyVector {

    @JsonIgnore
    private boolean angleNormalized;

    public BodyPosition setX(double x) {
        validateAndSetX(x);
        return this;
    }

    public BodyPosition setY(double y) {
        validateAndSetY(y);
        return this;
    }

    public BodyPosition setAngle(double angle) {
        if (angle < -Math.PI || angle > Math.PI) {
            validateAndSetAngle(Math.atan2(Math.sin(angle), Math.cos(angle)));
            this.angleNormalized = true;
        } else {
            validateAndSetAngle(angle);
            this.angleNormalized = false;
        }
        return this;
    }

    @JsonIgnore
    public Position getCenter() {
        return new Position()
                .setX(getX())
                .setY(getY());
    }

    @JsonIgnore
    public BodyPosition next(BodyVelocity velocity, double timeStep) {
        return new BodyPosition()
                .setX(getX() + velocity.getX() * timeStep)
                .setY(getY() + velocity.getY() * timeStep)
                .setAngle(getAngle() + velocity.getAngle() * timeStep);
    }

    public BodyPosition shifted(Shift shift) {
        return new BodyPosition()
                .setX(getX() + shift.distance() * Math.cos(shift.angle()))
                .setY(getY() + shift.distance() * Math.sin(shift.angle()))
                .setAngle(getAngle());
    }

    public static BodyPosition of(Position center, double angle) {
        return new BodyPosition()
                .setX(center.getX())
                .setY(center.getY())
                .setAngle(angle);
    }
}
