package com.github.aadvorak.artilleryonline.battle.common;

public interface Vector {

    double getX();

    double getY();

    default VectorProjections projections(double angle) {
        return new VectorProjections(angle)
                .setNormal(- getX() * Math.sin(angle) + getY() * Math.cos(angle))
                .setTangential(getX() * Math.cos(angle) + getY() * Math.sin(angle));
    }

    default double magnitude() {
        return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2));
    }
}
