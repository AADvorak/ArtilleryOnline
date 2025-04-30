package com.github.aadvorak.artilleryonline.battle.calculator.wheel;

import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelGroundState;

public class GroundStateCalculator {

    public static void calculate(WheelCalculations wheelCalculations) {
        if (wheelCalculations.getGroundContact() == null) {
            wheelCalculations.setGroundState(WheelGroundState.FULL_OVER_GROUND);
        } else {
            var depth = wheelCalculations.getGroundContact().depth();
            var radius = wheelCalculations.getModel().getSpecs().getWheelRadius();
            if (depth < radius / 2) {
                wheelCalculations.setGroundState(WheelGroundState.HALF_OVER_GROUND);
            } else if (depth < radius) {
                wheelCalculations.setGroundState(WheelGroundState.HALF_UNDER_GROUND);
            } else {
                wheelCalculations.setGroundState(WheelGroundState.FULL_UNDER_GROUND);
            }
        }
    }
}
