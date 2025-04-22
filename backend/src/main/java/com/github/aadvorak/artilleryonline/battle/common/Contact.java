package com.github.aadvorak.artilleryonline.battle.common;

public record Contact(double depth, double angle, Vector normal, Position position) {

    public Contact inverted() {
        return new Contact(depth, angle - Math.PI, normal.inverted(), position);
    }

    public static Contact of(double depth, Vector normal, Position position) {
        if (depth < Constants.INTERPENETRATION_THRESHOLD) {
            return null;
        }
        return new Contact(depth, normal.angleWithX() + Math.PI / 2, normal, position);
    }

    public static Contact withUncheckedDepthOf(double depth, Vector normal, Position position) {
        return new Contact(depth, normal.angleWithX() + Math.PI / 2, normal, position);
    }

    public static Contact of(double depth, double angle, Position position) {
        if (depth < Constants.INTERPENETRATION_THRESHOLD) {
            return null;
        }
        return new Contact(depth, angle, getNormal(angle), position);
    }

    private static Vector getNormal(double angle) {
        return new VectorImpl()
                .setX(Math.cos(angle - Math.PI / 2))
                .setY(Math.sin(angle - Math.PI / 2));
    }
}
