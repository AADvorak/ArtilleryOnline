package com.github.aadvorak.artilleryonline.battle.common;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Force implements Vector {

    private double x;

    private double y;

    @Override
    public String toString() {
        return String.format("(%.3f, %.3f)", x, y);
    }

    public static Force of(Vector vector) {
        return new Force()
                .setX(vector.getX())
                .setY(vector.getY());
    }
}
