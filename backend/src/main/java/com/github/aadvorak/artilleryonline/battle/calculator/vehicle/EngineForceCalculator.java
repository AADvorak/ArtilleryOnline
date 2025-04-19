package com.github.aadvorak.artilleryonline.battle.calculator.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.common.Force;
import com.github.aadvorak.artilleryonline.battle.common.JetType;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;

import java.util.List;

public class EngineForceCalculator implements ForceCalculator<
        VehicleSpecs,
        VehiclePreCalc,
        VehicleConfig,
        VehicleState,
        VehicleModel,
        VehicleCalculations> {

    private static final String FORCE_DESCRIPTION = "Engine";

    @Override
    public List<ForceAtPoint> calculate(VehicleCalculations calculations, BattleModel battleModel) {
        return List.of(
                calculateForWheel(calculations.getLeftWheel()),
                calculateForWheel(calculations.getRightWheel())
        );
    }

    private ForceAtPoint calculateForWheel(WheelCalculations calculations) {
        var vehicleModel = calculations.getModel();
        var jetState = vehicleModel.getState().getJetState();
        var jetSpecs = vehicleModel.getConfig().getJet();
        var direction = vehicleModel.getState().getMovingDirection();
        if (direction == null || vehicleModel.getState().getTrackState().isBroken()
                || jetSpecs != null && jetState != null && jetState.isActive() && jetSpecs.getType().equals(JetType.VERTICAL)
                || WheelGroundState.FULL_UNDER_GROUND.equals(calculations.getGroundState())
                || WheelGroundState.FULL_OVER_GROUND.equals(calculations.getGroundState())) {
            return ForceAtPoint.zero(FORCE_DESCRIPTION);
        }
        var depth = calculations.getGroundDepth();
        var groundAngle = calculations.getGroundAngle();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var depthCoefficient = 1 - depth * 0.5 / wheelRadius;
        var forceMagnitude = calculations.getMass() * depthCoefficient * vehicleModel.getSpecs().getAcceleration() / 2;
        var depthAngle = depth * Math.PI / (4 * wheelRadius);
        var force = new Force();
        if (MovingDirection.RIGHT.equals(direction)) {
            force
                    .setX(forceMagnitude * Math.cos(groundAngle + depthAngle))
                    .setY(forceMagnitude * Math.sin(groundAngle + depthAngle));

        }
        if (MovingDirection.LEFT.equals(direction)) {
            force
                    .setX( - forceMagnitude * Math.cos(groundAngle - depthAngle))
                    .setY( - forceMagnitude * Math.sin(groundAngle - depthAngle));
        }
        return new ForceAtPoint(force, calculations.getNearestGroundPoint().position(), FORCE_DESCRIPTION);
    }
}
