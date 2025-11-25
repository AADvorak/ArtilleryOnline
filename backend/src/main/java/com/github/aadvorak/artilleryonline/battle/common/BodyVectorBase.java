package com.github.aadvorak.artilleryonline.battle.common;

import lombok.Getter;

@Getter
public abstract class BodyVectorBase extends VectorBase {

    private double angle;

    protected void validateAndSetAngle(double angle) {
        validate(angle);
        this.angle = angle;
    }

    @Override
    public String toString() {
        return String.format("(%.6f, %.6f, %.6f)", getX(), getY(), getAngle());
    }
}
