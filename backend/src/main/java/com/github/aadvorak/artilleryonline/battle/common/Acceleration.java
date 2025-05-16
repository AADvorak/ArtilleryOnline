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
        return Acceleration.of(Vector.sumOf(accelerations));
    }

    public static Acceleration of(Vector vector) {
        return new Acceleration()
                .setX(vector.getX())
                .setY(vector.getY());
    }

    @Override
    public String toString() {
        return String.format("(%.3f, %.3f)", x, y);
    }
}
