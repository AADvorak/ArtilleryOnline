package com.github.aadvorak.artilleryonline.battle.calculator.wheel;

import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelGroundState;

public class GroundStateCalculator {

    public static void calculate(WheelCalculations wheelCalculations) {
        if (wheelCalculations.getNearestGroundPoint() == null) {
            if (wheelCalculations.getNearestGroundPointByX().getY() >= wheelCalculations.getPosition().getY()) {
                wheelCalculations.setGroundState(WheelGroundState.FULL_UNDER_GROUND);
            } else {
                wheelCalculations.setGroundState(WheelGroundState.FULL_OVER_GROUND);
            }
        } else {
            if (wheelCalculations.getNearestGroundPointByX().getY() >= wheelCalculations.getPosition().getY()) {
                wheelCalculations.setGroundState(WheelGroundState.HALF_UNDER_GROUND);
            } else {
                wheelCalculations.setGroundState(WheelGroundState.HALF_OVER_GROUND);
            }
        }
    }
}
