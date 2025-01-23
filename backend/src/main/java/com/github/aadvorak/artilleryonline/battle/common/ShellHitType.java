package com.github.aadvorak.artilleryonline.battle.common;

import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;

public enum ShellHitType {
    GROUND, VEHICLE_HULL, VEHICLE_TRACK;

    public static ShellHitType of(Calculations<?> calculations) {
        if (calculations == null) return GROUND;
        if (calculations instanceof VehicleCalculations) return VEHICLE_HULL;
        if (calculations instanceof WheelCalculations) return VEHICLE_TRACK;
        return null;
    }
}
