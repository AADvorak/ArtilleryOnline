package com.github.aadvorak.artilleryonline.battle.common;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Position implements Vector {

    private double x;

    private double y;

    public double distanceTo(Position other) {
        return Math.sqrt(Math.pow(x - other.getX(), 2.0) + Math.pow(y - other.getY(), 2.0));
    }

    public double angleTo(Position other) {
        var dx = other.getX() - x;
        var dy = other.getY() - y;
        return Math.atan2(dy, dx);
    }

    @Override
    public String toString() {
        return String.format("(%.3f, %.3f)", x, y);
    }
}
