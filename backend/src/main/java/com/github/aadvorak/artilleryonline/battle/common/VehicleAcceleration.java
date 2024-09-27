package com.github.aadvorak.artilleryonline.battle.common;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class VehicleAcceleration {

    private double x;

    private double y;

    private double angle;

    public Acceleration getMovingAcceleration() {
        return new Acceleration()
                .setX(x)
                .setY(y);
    }

    public VehicleAcceleration setMovingAcceleration(Acceleration acceleration) {
        x = acceleration.getX();
        y = acceleration.getY();
        return this;
    }
}
