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

import java.util.ArrayList;
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
    public List<BodyForce> calculate(VehicleCalculations calculations, BattleModel battleModel) {
        var forces = new ArrayList<BodyForce>();
        var leftWheelForce = calculateForWheel(calculations.getLeftWheel());
        var rightWheelForce = calculateForWheel(calculations.getRightWheel());
        if (leftWheelForce != null) {
            forces.add(leftWheelForce);
        }
        if (rightWheelForce != null) {
            forces.add(rightWheelForce);
        }
        return forces;
    }

    private BodyForce calculateForWheel(WheelCalculations calculations) {
        var vehicleModel = calculations.getModel();
        var jetState = vehicleModel.getState().getJetState();
        var jetSpecs = vehicleModel.getConfig().getJet();
        var direction = vehicleModel.getState().getMovingDirection();
        if (direction == null || vehicleModel.getState().getTrackState().isBroken()
                || jetSpecs != null && jetState != null && jetState.isActive() && jetSpecs.getType().equals(JetType.VERTICAL)
                || WheelGroundState.FULL_UNDER_GROUND.equals(calculations.getGroundState())
                || WheelGroundState.FULL_OVER_GROUND.equals(calculations.getGroundState())) {
            return null;
        }
        var depth = calculations.getGroundContact().depth();
        var groundAngle = calculations.getGroundContact().angle();
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
        return BodyForce.of(force, calculations.getGroundContact().position(),
                calculations.getModel().getState().getPosition().getCenter(), FORCE_DESCRIPTION);
    }
}
