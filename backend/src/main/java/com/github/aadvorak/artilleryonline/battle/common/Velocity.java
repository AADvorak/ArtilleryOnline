package com.github.aadvorak.artilleryonline.battle.common;

public class Velocity extends VectorBase implements Vector {

    public Velocity setX(double x) {
        validateAndSetX(x);
        return this;
    }

    public Velocity setY(double y) {
        validateAndSetY(y);
        return this;
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

    public static Velocity of(double magnitude, double angle) {
        return new Velocity()
                .setX(magnitude * Math.cos(angle))
                .setY(magnitude * Math.sin(angle));
    }
}
