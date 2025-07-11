package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.ForceCalculator;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.vehicle.*;
import com.github.aadvorak.artilleryonline.battle.common.BodyAcceleration;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;

import java.util.List;

public class VehicleAccelerationCalculator {

    private static final List<
            ForceCalculator<VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel, VehicleCalculations>
            > forceCalculators = List.of(
            new GravityForceCalculator(),
            new GroundFrictionForceCalculator(),
            new GroundReactionForceCalculator(),
            new JetForceCalculator(),
            new EngineForceCalculator()
    );

    private static final BodyAccelerationCalculator<
            VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel, VehicleCalculations
            > calculator = new BodyAccelerationCalculator<>(forceCalculators);

    public static BodyAcceleration getVehicleAcceleration(VehicleCalculations vehicle, BattleModel battleModel) {
        vehicle.calculateAllGroundContacts(battleModel.getRoom());
        return calculator.calculate(vehicle, battleModel);
    }
}
