package com.github.aadvorak.artilleryonline.battle.common;

import lombok.Getter;

@Getter
public abstract class VectorBase {

    private double x;

    private double y;

    protected void validateAndSetX(double x) {
        validate(x);
        this.x = x;
    }

    protected void validateAndSetY(double y) {
        validate(y);
        this.y = y;
    }

    protected void validate(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("The value is NaN or Infinite");
        }
    }

    @Override
    public String toString() {
        return String.format("(%.3f, %.3f)", getX(), getY());
    }
}
