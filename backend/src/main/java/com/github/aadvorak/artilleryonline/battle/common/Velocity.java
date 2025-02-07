package com.github.aadvorak.artilleryonline.battle.common;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Velocity implements Vector {

    private double x;

    private double y;

    @Override
    public String toString() {
        return String.format("(%.3f, %.3f)", x, y);
    }

    public static Velocity sumOf(Velocity... velocities) {
        var sumX = 0.0;
        var sumY = 0.0;
        for (var velocity : velocities) {
            sumX += velocity.getX();
            sumY += velocity.getY();
        }
        return new Velocity().setX(sumX).setY(sumY);
    }
}
