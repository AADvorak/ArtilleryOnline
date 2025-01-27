package com.github.aadvorak.artilleryonline.battle.common;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BodyAcceleration {

    private double x;

    private double y;

    private double angle;

    public Acceleration getMovingAcceleration() {
        return new Acceleration()
                .setX(x)
                .setY(y);
    }

    public BodyAcceleration setMovingAcceleration(Acceleration acceleration) {
        x = acceleration.getX();
        y = acceleration.getY();
        return this;
    }

    public static BodyAcceleration sumOf(BodyAcceleration... accelerations) {
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
