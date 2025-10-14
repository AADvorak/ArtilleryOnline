package com.github.aadvorak.artilleryonline.battle.common;

public class Acceleration extends VectorBase implements Vector {

    public Acceleration setX(double x) {
        validateAndSetX(x);
        return this;
    }

    public Acceleration setY(double y) {
        validateAndSetY(y);
        return this;
    }

    public void add(Acceleration acceleration) {
        setX(getX() + acceleration.getX());
        setY(getY() + acceleration.getY());
    }

    public static Acceleration sumOf(Acceleration... accelerations) {
        return Acceleration.of(Vector.sumOf(accelerations));
    }

    public static Acceleration of(Vector vector) {
        return new Acceleration()
                .setX(vector.getX())
                .setY(vector.getY());
    }
}
