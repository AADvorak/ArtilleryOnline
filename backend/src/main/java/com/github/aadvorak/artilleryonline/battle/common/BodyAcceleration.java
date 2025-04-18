package com.github.aadvorak.artilleryonline.battle.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class BodyAcceleration implements BodyVector {

    private double x;

    private double y;

    private double angle;

    @JsonIgnore
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
