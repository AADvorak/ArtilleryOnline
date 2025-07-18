package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ForceCalculator;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.BodyAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.vehicle.*;
import com.github.aadvorak.artilleryonline.battle.common.Constants;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.processor.Step1Processor;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VehicleMoveStep1Processor extends VehicleProcessor implements Step1Processor {

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

    @Override
    public void processVehicle(VehicleCalculations vehicle, BattleCalculations battle) {
        recalculateVelocity(vehicle, battle);
        vehicle.calculateNextPosition(battle.getModel().getCurrentTimeStepSecs());
        vehicle.recalculateWheelsVelocities();
    }

    private void recalculateVelocity(VehicleCalculations vehicle, BattleCalculations battle) {
        vehicle.calculateAllGroundContacts(battle.getModel().getRoom());
        var acceleration = calculator.calculate(vehicle, battle.getModel());
        var vehicleVelocity = vehicle.getModel().getState().getVelocity();
        var timeStep = battle.getModel().getCurrentTimeStepSecs();
        var maxRadius = vehicle.getModel().getPreCalc().getMaxRadius();
        vehicleVelocity.recalculate(acceleration, timeStep);
        if (Math.abs(vehicleVelocity.getX()) < Constants.MIN_VELOCITY) {
            vehicleVelocity.setX(0.0);
        }
        if (Math.abs(vehicleVelocity.getY()) < Constants.MIN_VELOCITY) {
            vehicleVelocity.setY(0.0);
        }
        if (Math.abs(vehicleVelocity.getAngle() * maxRadius) < Constants.MIN_VELOCITY) {
            vehicleVelocity.setAngle(0.0);
        }
    }
}
