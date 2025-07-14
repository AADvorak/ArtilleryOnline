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
        if (JetType.VERTICAL.equals(jetSpecs.getType())) {
            addVertical(forces, calculations.getLeftWheel(), acceleration / 2, angle, direction);
            addVertical(forces, calculations.getRightWheel(), acceleration / 2, angle, direction);
        }
        if (JetType.HORIZONTAL.equals(jetSpecs.getType())) {
            addHorizontal(forces, acceleration, angle, direction);
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
        if (MovingDirection.RIGHT.equals(direction)) {
            var force = new Force()
                    .setX(acceleration * Math.cos(angle + HORIZONTAL_JET_ANGLE))
                    .setY(acceleration * Math.sin(angle + HORIZONTAL_JET_ANGLE));
            forces.add(BodyForce.atCOM(force, FORCE_DESCRIPTION));
        }
        if (MovingDirection.LEFT.equals(direction)) {
            var force = new Force()
                    .setX(acceleration * Math.cos(angle - HORIZONTAL_JET_ANGLE + Math.PI))
                    .setY(acceleration * Math.sin(angle - HORIZONTAL_JET_ANGLE + Math.PI));
            forces.add(BodyForce.atCOM(force, FORCE_DESCRIPTION));
        }
    }
}
