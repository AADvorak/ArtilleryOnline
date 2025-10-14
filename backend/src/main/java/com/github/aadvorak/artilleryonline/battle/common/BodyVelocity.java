package com.github.aadvorak.artilleryonline.battle.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BodyVelocity extends BodyVectorBase implements BodyVector {

    public BodyVelocity setX(double x) {
        validateAndSetX(x);
        return this;
    }

    public BodyVelocity setY(double y) {
        validateAndSetY(y);
        return this;
    }

    public BodyVelocity setAngle(double angle) {
        validateAndSetAngle(angle);
        return this;
    }

    @JsonIgnore
    public Velocity getMovingVelocity() {
        return new Velocity()
                .setX(getX())
                .setY(getY());
    }

    public void recalculate(BodyAcceleration acceleration, double timeStep) {
        validateAndSetX(getX() + acceleration.getX() * timeStep);
        validateAndSetY(getY() + acceleration.getY() * timeStep);
        validateAndSetAngle(getAngle() + acceleration.getAngle() * timeStep);
    }

    public Velocity getPointVelocity(double pointDistance, double pointAngle) {
        var angleVelocity = getAngle() * pointDistance;
        return new Velocity()
                .setX(getX() - angleVelocity * Math.sin(pointAngle))
                .setY(getY() + angleVelocity * Math.cos(pointAngle));
    }

    public static BodyVelocity of(BodyVelocity velocity) {
        return new BodyVelocity()
                .setX(velocity.getX())
                .setY(velocity.getY())
                .setAngle(velocity.getAngle());
    }
}
