package com.github.aadvorak.artilleryonline.battle.common;

public record Interpenetration(double depth, double angle) {

    public Interpenetration inverted() {
        return new Interpenetration(depth, angle - Math.PI);
    }

    public static Interpenetration withUncheckedDepth(double depth, Position position, Position otherPosition) {
        return new Interpenetration(depth, getAngle(position, otherPosition));
    }

    public static Interpenetration of(double depth, double angle) {
        if (depth < Constants.INTERPENETRATION_THRESHOLD) {
            return null;
        }
        return new Interpenetration(depth, angle);
    }

    public static Interpenetration of(double depth, Position position, Position otherPosition) {
        if (depth < Constants.INTERPENETRATION_THRESHOLD) {
            return null;
        }
        return new Interpenetration(depth, getAngle(position, otherPosition));
    }

    private static double getAngle(Position position, Position otherPosition) {
        var dx = otherPosition.getX() - position.getX();
        var dy = otherPosition.getY() - position.getY();
        return Math.atan2(dy, dx) + Math.PI / 2;
    }
}
