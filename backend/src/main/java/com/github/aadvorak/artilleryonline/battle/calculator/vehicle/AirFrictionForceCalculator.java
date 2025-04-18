package com.github.aadvorak.artilleryonline.battle.calculator.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.ForceAtPoint;
import com.github.aadvorak.artilleryonline.battle.calculations.ForceCalculator;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Force;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;

import java.util.List;

public class AirFrictionForceCalculator implements ForceCalculator<
        VehicleSpecs,
        VehiclePreCalc,
        VehicleConfig,
        VehicleState,
        VehicleModel,
        VehicleCalculations> {

    @Override
    public List<ForceAtPoint> calculate(VehicleCalculations calculations, BattleModel battleModel) {
        var frictionCoefficient = battleModel.getRoom().getSpecs().getAirFrictionCoefficient();
        var velocity = calculations.getVelocity();
        // todo remove mass with size coefficient
        var mass = calculations.getMass();
        var frictionForce = new Force()
                .setX( - velocity.getX() * Math.abs(velocity.getX()) * frictionCoefficient * mass)
                .setY( - velocity.getY() * Math.abs(velocity.getY()) * frictionCoefficient * mass);
        return List.of(ForceAtPoint.atCOM(frictionForce));
    }
}
