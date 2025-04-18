package com.github.aadvorak.artilleryonline.battle.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@RequiredArgsConstructor
@Accessors(chain = true)
public class VectorProjections {

    private final double angle;

    private double normal;

    private double tangential;

    public static VectorProjections copyOf(VectorProjections vectorProjections) {
        return new VectorProjections(vectorProjections.getAngle())
                .setNormal(vectorProjections.getNormal())
                .setTangential(vectorProjections.getTangential());
    }

    public Force recoverForce() {
        return new Force()
                .setX(getX())
                .setY(getY());
    }

    public Acceleration recoverAcceleration() {
        return new Acceleration()
                .setX(getX())
                .setY(getY());
    }

    public Velocity recoverVelocity() {
        return new Velocity()
                .setX(getX())
                .setY(getY());
    }

    public Position recoverPosition() {
        return new Position()
                .setX(getX())
                .setY(getY());
    }

    private double getX() {
        return - normal * Math.sin(angle) + tangential * Math.cos(angle);
    }

    private double getY() {
        return normal * Math.cos(angle) + tangential * Math.sin(angle);
    }
}
