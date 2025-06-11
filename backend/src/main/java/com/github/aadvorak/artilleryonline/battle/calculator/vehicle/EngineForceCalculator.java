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
        var contactsNumber = calculations.getAllGroundContacts().size();
        var leftWheelForce = calculateForWheel(calculations.getLeftWheel(), contactsNumber);
        var rightWheelForce = calculateForWheel(calculations.getRightWheel(), contactsNumber);
        if (leftWheelForce != null) {
            forces.add(leftWheelForce);
        }
        if (rightWheelForce != null) {
            forces.add(rightWheelForce);
        }
        return forces;
    }

    private BodyForce calculateForWheel(WheelCalculations calculations, int contactsNumber) {
        var vehicleModel = calculations.getModel();
        var jetState = vehicleModel.getState().getJetState();
        var jetSpecs = vehicleModel.getConfig().getJet();
        var direction = vehicleModel.getState().getMovingDirection();
        var contact = calculations.getGroundContact();
        if (direction == null || vehicleModel.getState().getTrackState().isBroken()
                || jetSpecs != null && jetState != null && jetState.isActive() && jetSpecs.getType().equals(JetType.VERTICAL)
                || contact == null) {
            return null;
        }
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var forceMagnitude = calculations.getMass() * vehicleModel.getSpecs().getAcceleration()
                * Math.cos(contact.angle()) / contactsNumber;
        var depthAngle = contact.depth() * Math.PI / (4 * wheelRadius);
        var force = new Force();
        if (MovingDirection.RIGHT.equals(direction)) {
            force
                    .setX(forceMagnitude * Math.cos(contact.angle() + depthAngle))
                    .setY(forceMagnitude * Math.sin(contact.angle() + depthAngle));

        }
        if (MovingDirection.LEFT.equals(direction)) {
            force
                    .setX( - forceMagnitude * Math.cos(contact.angle() - depthAngle))
                    .setY( - forceMagnitude * Math.sin(contact.angle() - depthAngle));
        }
        return BodyForce.of(force, calculations.getPosition(),
                calculations.getModel().getState().getPosition().getCenter(), FORCE_DESCRIPTION);
    }
}
