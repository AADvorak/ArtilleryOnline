package com.github.aadvorak.artilleryonline.battle.common;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class Acceleration {

    private double x;

    private double y;

    public static Acceleration sumOf(List<Acceleration> accelerations) {
        var sumX = 0.0;
        var sumY = 0.0;
        for (var acceleration : accelerations) {
            sumX += acceleration.getX();
            sumY += acceleration.getY();
        }
        return new Acceleration().setX(sumX).setY(sumY);
    }
}
