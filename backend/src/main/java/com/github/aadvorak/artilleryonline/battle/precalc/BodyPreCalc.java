package com.github.aadvorak.artilleryonline.battle.precalc;

import com.github.aadvorak.artilleryonline.battle.common.Shift;

public interface BodyPreCalc {

    double getMass();

    double getMomentOfInertia();

    Shift getCenterOfMassShift();
}
