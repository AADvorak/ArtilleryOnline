package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.ForceCalculator;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.vehicle.*;
import com.github.aadvorak.artilleryonline.battle.calculator.wheel.*;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;

import java.util.List;

public class VehicleAccelerationCalculator {

    private static final List<
            ForceCalculator<VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel, VehicleCalculations>
            > forceCalculators = List.of(
            new AirFrictionForceCalculator(),
            new EngineForceCalculator(),
            new GravityForceCalculator(),
            new GroundFrictionForceCalculator(),
            new GroundReactionForceCalculator(),
            new JetForceCalculator()
    );

    private static final BodyAccelerationCalculator<
            VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel, VehicleCalculations
            > calculator = new BodyAccelerationCalculator<>(forceCalculators);

    public static BodyAcceleration getVehicleAcceleration(VehicleCalculations vehicle, BattleModel battleModel) {
        vehicle.recalculateWheelsVelocities();

        calculateGroundPositionAndState(vehicle.getRightWheel(), vehicle.getModel(), battleModel.getRoom());
        calculateGroundPositionAndState(vehicle.getLeftWheel(), vehicle.getModel(), battleModel.getRoom());

        var acceleration = calculator.calculate(vehicle, battleModel);
        var angleVelocity = vehicle.getModel().getState().getVelocity().getAngle();
        acceleration.setAngle(acceleration.getAngle() - angleVelocity);

        return acceleration;
    }

    private static void calculateGroundPositionAndState(WheelCalculations wheelCalculations,
                                                        VehicleModel vehicleModel, RoomModel roomModel) {
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        GroundPositionCalculator.calculate(wheelCalculations, wheelRadius, roomModel);
        GroundStateCalculator.calculate(wheelCalculations);
    }
}
