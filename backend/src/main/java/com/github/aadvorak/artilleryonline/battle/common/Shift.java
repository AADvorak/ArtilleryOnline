package com.github.aadvorak.artilleryonline.battle.common;

public record Shift(double distance, double angle) {

    public Shift inverted() {
        return new Shift(-distance, angle);
    }

    public Shift plusAngle(double addAngle) {
        return new Shift(distance, angle + addAngle);
    }
}
