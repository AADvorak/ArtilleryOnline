package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Acceleration;
import com.github.aadvorak.artilleryonline.battle.common.BodyAcceleration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissileAccelerations {

    private Acceleration pushing = new Acceleration();

    private BodyAcceleration friction = new BodyAcceleration();

    private Acceleration gravity = new Acceleration();

    private double returning;

    private double correcting;

    public BodyAcceleration sum() {
        var linear = Acceleration.sumOf(pushing, gravity);
        var angular = returning + correcting;
        var acceleration = new BodyAcceleration()
                .setX(linear.getX())
                .setY(linear.getY())
                .setAngle(angular);
        return BodyAcceleration.sumOf(acceleration, friction);
    }
}
