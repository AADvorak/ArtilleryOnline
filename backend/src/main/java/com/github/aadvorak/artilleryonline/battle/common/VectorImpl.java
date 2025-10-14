package com.github.aadvorak.artilleryonline.battle.common;

public class VectorImpl extends VectorBase implements Vector {

    public VectorImpl setX(double x) {
        validateAndSetX(x);
        return this;
    }

    public VectorImpl setY(double y) {
        validateAndSetY(y);
        return this;
    }
}
