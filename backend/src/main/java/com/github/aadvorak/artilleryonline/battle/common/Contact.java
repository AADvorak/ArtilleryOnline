package com.github.aadvorak.artilleryonline.battle.common;

public record Contact(double depth, double angle, Vector normal, Position position, String description) {

    public Contact inverted() {
        return new Contact(depth, angle - Math.PI, normal.inverted(), position, description);
    }

    public static Contact of(double depth, Vector normal, Position position) {
        return Contact.of(depth, normal, position, null);
    }

    public static Contact of(double depth, Vector normal, Position position, String description) {
        if (depth < Constants.INTERPENETRATION_THRESHOLD) {
            return null;
        }
        return new Contact(depth, normal.angle() + Math.PI / 2, normal, position, description);
    }

    public static Contact withUncheckedDepthOf(double depth, double angle, Position position) {
        return Contact.withUncheckedDepthOf(depth, angle, position, null);
    }

    public static Contact withUncheckedDepthOf(double depth, double angle, Position position, String description) {
        return new Contact(depth, angle, Vector.normal(angle), position, description);
    }

    public static Contact withUncheckedDepthOf(double depth, Vector normal, Position position) {
        return Contact.withUncheckedDepthOf(depth, normal, position, null);
    }

    public static Contact withUncheckedDepthOf(double depth, Vector normal, Position position, String description) {
        return new Contact(depth, normal.angle() + Math.PI / 2, normal, position, description);
    }

    public static Contact of(double depth, double angle, Position position) {
        return Contact.of(depth, angle, position, null);
    }

    public static Contact of(double depth, double angle, Position position, String description) {
        if (depth < Constants.INTERPENETRATION_THRESHOLD) {
            return null;
        }
        return new Contact(depth, angle, Vector.normal(angle), position, description);
    }

    @Override
    public String toString() {
        return String.format("Contact [depth=%.6f, angle=%.6f, normal=%s, position=%s, %s]",
                depth, angle, normal, position, description);
    }
}
