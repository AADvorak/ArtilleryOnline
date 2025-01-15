package com.github.aadvorak.artilleryonline.battle.common;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Acceleration implements Vector {

    private double x;

    private double y;

    public void add(Acceleration acceleration) {
        x += acceleration.x;
        y += acceleration.y;
    }

    public static Acceleration sumOf(Acceleration... accelerations) {
        var sumX = 0.0;
        var sumY = 0.0;
        for (var acceleration : accelerations) {
            sumX += acceleration.getX();
            sumY += acceleration.getY();
        }
        return new Acceleration().setX(sumX).setY(sumY);
    }

    @Override
    public String toString() {
        return String.format("(%.3f, %.3f)", x, y);
    }
}
