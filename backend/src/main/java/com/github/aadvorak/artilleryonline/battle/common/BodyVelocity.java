package com.github.aadvorak.artilleryonline.battle.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BodyVelocity {

    private double x;

    private double y;

    private double angle;

    @JsonIgnore
    public Velocity getMovingVelocity() {
        return new Velocity()
                .setX(x)
                .setY(y);
    }

    public void recalculate(BodyAcceleration acceleration, double timeStep) {
        setX(x + acceleration.getX() * timeStep);
        setY(y + acceleration.getY() * timeStep);
        setAngle(angle + acceleration.getAngle() * timeStep);
    }

    public Velocity getPointVelocity(double pointDistance, double pointAngle) {
        var angleVelocity = angle * pointDistance;
        return new Velocity()
                .setX(x - angleVelocity * Math.sin(pointAngle))
                .setY(y + angleVelocity * Math.cos(pointAngle));
    }

    @Override
    public String toString() {
        return String.format("(%.3f, %.3f, %.3f)", x, y, angle);
    }

    public static BodyVelocity of(BodyVelocity velocity) {
        return new BodyVelocity()
                .setX(velocity.getX())
                .setY(velocity.getY())
                .setAngle(velocity.getAngle());
    }
}
