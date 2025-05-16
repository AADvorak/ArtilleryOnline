package com.github.aadvorak.artilleryonline.battle.common;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;

public interface Vector extends CompactSerializable {

    double getX();

    double getY();

    static Vector normal(double angle) {
        return new VectorImpl()
                .setX(Math.cos(angle - Math.PI / 2))
                .setY(Math.sin(angle - Math.PI / 2));
    }

    static Vector tangential(double angle) {
        return new VectorImpl()
                .setX(Math.cos(angle))
                .setY(Math.sin(angle));
    }

    static Vector sumOf(Vector... vectors) {
        var sumX = 0.0;
        var sumY = 0.0;
        for (var vector : vectors) {
            sumX += vector.getX();
            sumY += vector.getY();
        }
        return new VectorImpl().setX(sumX).setY(sumY);
    }

    default VectorProjections projections(double angle) {
        return new VectorProjections(angle)
                .setNormal(getX() * Math.sin(angle) - getY() * Math.cos(angle))
                .setTangential(getX() * Math.cos(angle) + getY() * Math.sin(angle));
    }

    default double magnitude() {
        return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2));
    }

    default double angle() {
        return Math.atan2(getY(), getX());
    }

    default double dotProduct(Vector vector) {
        return getX() * vector.getX() + getY() * vector.getY();
    }

    default double vectorProduct(Vector vector) {
        return getX() * vector.getY() - getY() * vector.getX();
    }

    default Vector projectionOnto(Vector vector) {
        var dotProduct = dotProduct(vector);
        var magnitudeSquared = Math.pow(vector.magnitude(), 2);
        if (magnitudeSquared == 0) {
            return new VectorImpl()
                    .setX(getX())
                    .setY(getY());
        }
        var scalar = dotProduct / magnitudeSquared;
        return new VectorImpl()
                .setX(scalar * vector.getX())
                .setY(scalar * vector.getY());
    }

    default Vector normalized() {
        var magnitude = magnitude();
        return new VectorImpl()
                .setX(getX() / magnitude)
                .setY(getY() / magnitude);
    }

    default Vector multiply(double scalar) {
        return new VectorImpl()
                .setX(getX() * scalar)
                .setY(getY() * scalar);
    }

    default Vector inverted() {
        return new VectorImpl()
                .setX(-getX())
                .setY(-getY());
    }

    default double angleWithX() {
        return Math.atan2(getY(), getX());
    }

    @Override
    default void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeDouble(getX());
        stream.writeDouble(getY());
    }
}
