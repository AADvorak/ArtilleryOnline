package com.github.aadvorak.artilleryonline.battle.common;

import java.util.Arrays;
import java.util.List;

public class BodyAcceleration extends BodyVectorBase implements BodyVector {

    public BodyAcceleration setX(double x) {
        validateAndSetX(x);
        return this;
    }

    public BodyAcceleration setY(double y) {
        validateAndSetY(y);
        return this;
    }

    public BodyAcceleration setAngle(double angle) {
        validateAndSetAngle(angle);
        return this;
    }

    public static BodyAcceleration sumOf(BodyAcceleration... accelerations) {
        return sumOf(Arrays.stream(accelerations).toList());
    }

    public static BodyAcceleration sumOf(List<BodyAcceleration> accelerations) {
        var sumX = 0.0;
        var sumY = 0.0;
        var sumAngle = 0.0;
        for (var acceleration : accelerations) {
            sumX += acceleration.getX();
            sumY += acceleration.getY();
            sumAngle += acceleration.getAngle();
        }
        return new BodyAcceleration()
                .setX(sumX)
                .setY(sumY)
                .setAngle(sumAngle);
    }
}
