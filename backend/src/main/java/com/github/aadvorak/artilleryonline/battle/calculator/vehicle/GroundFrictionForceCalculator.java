package com.github.aadvorak.artilleryonline.battle.calculator.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.common.Force;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;

import java.util.ArrayList;
import java.util.List;

public class GroundFrictionForceCalculator implements ForceCalculator<
        VehicleSpecs,
        VehiclePreCalc,
        VehicleConfig,
        VehicleState,
        VehicleModel,
        VehicleCalculations> {

    private static final String FORCE_DESCRIPTION = "Ground Friction";

    @Override
    public List<ForceAtPoint> calculate(VehicleCalculations calculations, BattleModel battleModel) {
        var groundFrictionCoefficient = battleModel.getRoom().getSpecs().getGroundFrictionCoefficient();
        var forces = new ArrayList<ForceAtPoint>();
        addWheelFriction(forces, calculations.getRightWheel(), groundFrictionCoefficient);
        addWheelFriction(forces, calculations.getLeftWheel(), groundFrictionCoefficient);
        return forces;
    }

    private void addWheelFriction(List<ForceAtPoint> forces, WheelCalculations wheelCalculations,
                                 double groundFrictionCoefficient) {
        if (WheelGroundState.FULL_OVER_GROUND.equals(wheelCalculations.getGroundState())) {
            return;
        }
        var depth = WheelGroundState.FULL_UNDER_GROUND.equals(wheelCalculations.getGroundState())
                ? 2 * wheelCalculations.getModel().getSpecs().getWheelRadius()
                : wheelCalculations.getGroundContact().depth();
        var force = new Force()
                .setX( - wheelCalculations.getVelocity().getX() * depth * groundFrictionCoefficient)
                .setY( - wheelCalculations.getVelocity().getY() * depth * groundFrictionCoefficient);
        forces.add(new ForceAtPoint(force, wheelCalculations.getGroundContact().position(), FORCE_DESCRIPTION));
    }
}
