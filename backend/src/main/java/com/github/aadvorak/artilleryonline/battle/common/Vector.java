package com.github.aadvorak.artilleryonline.battle.common;

public interface Vector {

    double getX();

    double getY();

    default VectorProjections getProjections(double angle) {
        return new VectorProjections(angle)
                .setNormal(- getX() * Math.sin(angle) + getY() * Math.cos(angle))
                .setTangential(getX() * Math.cos(angle) + getY() * Math.sin(angle));
    }
}
