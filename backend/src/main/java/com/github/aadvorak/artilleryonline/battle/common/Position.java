package com.github.aadvorak.artilleryonline.battle.common;

public class Position extends VectorBase implements Vector {

    public Position setX(double x) {
        validateAndSetX(x);
        return this;
    }

    public Position setY(double y) {
        validateAndSetY(y);
        return this;
    }

    public double distanceTo(Position other) {
        return Math.sqrt(Math.pow(getX() - other.getX(), 2.0) + Math.pow(getY() - other.getY(), 2.0));
    }

    public double angleTo(Position other) {
        var dx = other.getX() - getX();
        var dy = other.getY() - getY();
        return Math.atan2(dy, dx);
    }

    public Vector vectorTo(Position other) {
        return new VectorImpl()
                .setX(other.getX() - getX())
                .setY(other.getY() - getY());
    }

    public Position shifted(double distance, double angle) {
        return shifted(new Shift(distance, angle));
    }

    public Position shifted(Shift shift) {
        return new Position()
                .setX(getX() + shift.distance() * Math.cos(shift.angle()))
                .setY(getY() + shift.distance() * Math.sin(shift.angle()));
    }

    public Position shifted(Vector vector) {
        return new Position()
                .setX(getX() + vector.getX())
                .setY(getY() + vector.getY());
    }
}
