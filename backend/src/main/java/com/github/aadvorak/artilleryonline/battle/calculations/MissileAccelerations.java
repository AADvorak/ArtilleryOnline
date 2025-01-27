package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Acceleration;
import com.github.aadvorak.artilleryonline.battle.common.BodyAcceleration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissileAccelerations {

    private Acceleration pushing = new Acceleration();

    private Acceleration friction = new Acceleration();

    private Acceleration gravity = new Acceleration();

    private double returning;

    private double correcting;

    public BodyAcceleration sum() {
        var linear = Acceleration.sumOf(pushing, friction);
        var angular = returning + correcting;
        return new BodyAcceleration()
                .setX(linear.getX())
                .setY(linear.getY())
                .setAngle(angular);
    }
}
