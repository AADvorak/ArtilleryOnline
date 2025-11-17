package com.github.aadvorak.artilleryonline.battle.calculator.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BodyForce;
import com.github.aadvorak.artilleryonline.battle.calculations.ForceCalculator;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
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

public class JetForceCalculator implements ForceCalculator<
        VehicleSpecs,
        VehiclePreCalc,
        VehicleConfig,
        VehicleState,
        VehicleModel,
        VehicleCalculations> {

    public static final String FORCE_DESCRIPTION = "Jet";
    public static final double HORIZONTAL_JET_ANGLE = Math.PI / 16;

    @Override
    public List<BodyForce> calculate(VehicleCalculations calculations, BattleModel battleModel) {
        var forces = new ArrayList<BodyForce>();
        var vehicleModel = calculations.getModel();
        var jetSpecs = vehicleModel.getConfig().getJet();
        if (jetSpecs == null) {
            return forces;
        }
        var jetState = vehicleModel.getState().getJetState();
        if (!jetState.isActive() || jetState.getVolume() <= 0) {
            return forces;
        }
        var acceleration = jetSpecs.getAcceleration() * calculations.getMass();
        var direction = vehicleModel.getState().getMovingDirection();
        var angle = vehicleModel.getState().getPosition().getAngle();
        if (vehicleModel.getState().isAboutToTurnOver()) {
            addTurning(forces, calculations, acceleration, angle);
        } else if (JetType.VERTICAL.equals(jetSpecs.getType())) {
            addVertical(forces, calculations.getLeftWheel(), acceleration / 2, angle, direction);
            addVertical(forces, calculations.getRightWheel(), acceleration / 2, angle, direction);
        }
        if (JetType.HORIZONTAL.equals(jetSpecs.getType())) {
            addHorizontalForWheel(forces, calculations.getRightWheel(), acceleration / 2, direction);
            addHorizontalForWheel(forces, calculations.getLeftWheel(), acceleration / 2, direction);
            if (forces.isEmpty()) {
                addHorizontal(forces, acceleration, angle, direction);
            } else if (forces.size() == 1) {
                addHorizontal(forces, acceleration / 2, angle, direction);
            }
        }
        return forces;
    }

    private static void addVertical(List<BodyForce> forces, WheelCalculations wheelCalculations,
                                    double acceleration, double angle, MovingDirection direction) {
        var angleCoefficient = 1 + wheelCalculations.getSign().getValue() * Math.sin(angle);
        var force = new Force();
        if (direction == null) {
            force
                    .setX(0.0)
                    .setY(acceleration * angleCoefficient);
        }
        if (MovingDirection.RIGHT.equals(direction)) {
            force
                    .setX(acceleration / Math.sqrt(2))
                    .setY(acceleration * angleCoefficient / Math.sqrt(2));
        }
        if (MovingDirection.LEFT.equals(direction)) {
            force
                    .setX(-acceleration / Math.sqrt(2))
                    .setY(acceleration * angleCoefficient / Math.sqrt(2));
        }
        forces.add(BodyForce.of(force, wheelCalculations.getPosition(),
                wheelCalculations.getModel().getState().getPosition().getCenter(), FORCE_DESCRIPTION));
    }

    private void addHorizontal(List<BodyForce> forces, double acceleration,
                               double angle, MovingDirection direction) {
        var additionalAngle = getHorizontalJetAdditionalAngle(angle);
        if (MovingDirection.RIGHT.equals(direction)) {
            var force = new Force()
                    .setX(acceleration * Math.cos(angle + additionalAngle))
                    .setY(acceleration * Math.sin(angle + additionalAngle));
            forces.add(BodyForce.atCOM(force, FORCE_DESCRIPTION));
        }
        if (MovingDirection.LEFT.equals(direction)) {
            var force = new Force()
                    .setX(acceleration * Math.cos(angle - additionalAngle + Math.PI))
                    .setY(acceleration * Math.sin(angle - additionalAngle + Math.PI));
            forces.add(BodyForce.atCOM(force, FORCE_DESCRIPTION));
        }
    }

    private void addHorizontalForWheel(List<BodyForce> forces, WheelCalculations calculations, double magnitude,
                                            MovingDirection direction) {
        var contact = calculations.getGroundContact();
        if (contact == null) {
            return;
        }
        var force = new Force();
        if (MovingDirection.RIGHT.equals(direction)) {
            force
                    .setX(magnitude * Math.cos(contact.angle() + HORIZONTAL_JET_ANGLE))
                    .setY(magnitude * Math.sin(contact.angle() + HORIZONTAL_JET_ANGLE));

        }
        if (MovingDirection.LEFT.equals(direction)) {
            force
                    .setX( - magnitude * Math.cos(contact.angle() - HORIZONTAL_JET_ANGLE))
                    .setY( - magnitude * Math.sin(contact.angle() - HORIZONTAL_JET_ANGLE));
        }
        forces.add(BodyForce.of(force, calculations.getPosition(),
                calculations.getModel().getState().getPosition().getCenter(), FORCE_DESCRIPTION));
    }

    private double getHorizontalJetAdditionalAngle(double angle) {
        var absAngle = Math.abs(angle);
        if (absAngle > Math.PI / 2) {
            return angle;
        }
        var absAdditionalAngle = HORIZONTAL_JET_ANGLE - 4 * HORIZONTAL_JET_ANGLE * absAngle / Math.PI;
        return absAngle < Math.PI / 4 ? absAdditionalAngle : -absAdditionalAngle;
    }

    private void addTurning(List<BodyForce> forces, VehicleCalculations calculations,
                            double acceleration, double angle) {
        var maxRadius = calculations.getModel().getPreCalc().getMaxRadius();
        var comPosition = calculations.getPosition();
        var position = comPosition.shifted(- maxRadius * Math.signum(angle), 0.0);
        forces.add(BodyForce.of(new Force().setY(acceleration * 1.5), position, comPosition, FORCE_DESCRIPTION));
        forces.add(BodyForce.atCOM(new Force().setY(acceleration * 0.5), FORCE_DESCRIPTION));
    }
}
