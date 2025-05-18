package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.BodyCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.BodyForce;
import com.github.aadvorak.artilleryonline.battle.calculations.ForceCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.vehicle.JetForceCalculator;
import com.github.aadvorak.artilleryonline.battle.common.BodyAcceleration;
import com.github.aadvorak.artilleryonline.battle.common.Force;
import com.github.aadvorak.artilleryonline.battle.common.Vector;
import com.github.aadvorak.artilleryonline.battle.config.Config;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.BodyModel;
import com.github.aadvorak.artilleryonline.battle.precalc.BodyPreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.Specs;
import com.github.aadvorak.artilleryonline.battle.state.BodyState;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BodyAccelerationCalculator<
        S extends Specs,
        P extends BodyPreCalc,
        Cf extends Config,
        St extends BodyState,
        M extends BodyModel<S, P, Cf, St>,
        C extends BodyCalculations<S, P, Cf, St, M>> {

    private static final double COLLIDER_PUSHING = 5.0;
    private static final double COLLIDER_ROTATING = 2.0;
    private static final List<String> FORCES_TO_EXTRACT_MOVING_FROM_ROTATING = List.of(
            JetForceCalculator.FORCE_DESCRIPTION
    );

    private final List<ForceCalculator<S, P, Cf, St, M, C>> forceCalculators;

    public BodyAcceleration calculate(C calculations, BattleModel battleModel) {
        List<BodyForce> forces = new ArrayList<>();
        List<BodyAcceleration> accelerations = new ArrayList<>();
        forceCalculators.forEach(forceCalculator ->
                forces.addAll(forceCalculator.calculate(calculations, battleModel)));
        extractMovingFromRotating(forces);
        forces.forEach(force -> accelerations.add(toAcceleration(force, calculations)));
        addColliderAcceleration(accelerations, calculations);
        return BodyAcceleration.sumOf(accelerations);
    }

    private BodyAcceleration toAcceleration(BodyForce force, C calculations) {
        var mass = calculations.getMass();
        var acceleration = new BodyAcceleration();
        if (force.moving() != null) {
            return acceleration
                    .setX(force.moving().getX() / mass)
                    .setY(force.moving().getY() / mass);
        }
        if (force.rotating() != null && force.radiusVector() != null) {
            var momentOfInertia = calculations.getModel().getPreCalc().getMomentOfInertia();
            acceleration.setAngle(force.torque() / momentOfInertia);
        }
        return acceleration;
    }

    private void extractMovingFromRotating(List<BodyForce> forces) {
        var extractedForces = new ArrayList<BodyForce>();
        for (var i = 0; i < forces.size(); i++) {
            BodyForce force1 = forces.get(i);
            for (var j = i + 1; j < forces.size(); j++) {
                BodyForce force2 = forces.get(j);
                if (canExtractMovingFromRotating(force1, force2)) {
                    var extractedForce = extractMovingFromRotating(force1, force2);
                    if (extractedForce != null) {
                        extractedForces.add(extractedForce);
                    }
                }
            }
        }
        forces.addAll(extractedForces);
        if (!extractedForces.isEmpty()) {
            System.out.println(extractedForces.size() + " forces extracted");
        }
    }

    private boolean canExtractMovingFromRotating(BodyForce force1, BodyForce force2) {
        return force1.rotating() != null && force2.rotating() != null
                && FORCES_TO_EXTRACT_MOVING_FROM_ROTATING.contains(force1.description())
                && FORCES_TO_EXTRACT_MOVING_FROM_ROTATING.contains(force2.description());
    }

    private BodyForce extractMovingFromRotating(BodyForce force1, BodyForce force2) {
        var torque1 = force1.torque();
        var torque2 = force2.torque();
        if (torque1 * torque2 >= 0) {
            return null;
        }
        var absTorque1 = Math.abs(torque1);
        var absTorque2 = Math.abs(torque2);
        if (absTorque1 > absTorque2) {
            return extractMovingFromRotating(force1, force2, absTorque1, absTorque2);
        } else {
            return extractMovingFromRotating(force2, force1, absTorque2, absTorque1);
        }
    }

    private BodyForce extractMovingFromRotating(BodyForce greater, BodyForce lower,
                                                 double greaterTorque, double lowerTorque) {
        var diff = greaterTorque - lowerTorque;
        var rotatingPart = diff / greaterTorque;
        var movingPart = (greaterTorque - diff) / greaterTorque;
        var newRotating = Force.of(greater.rotating().multiply(rotatingPart));
        var movingFromGreater = Force.of(greater.rotating().multiply(movingPart));
        var movingFromLower = lower.rotating();
        greater.setRotating(newRotating);
        lower.setRotating(null);
        return BodyForce.atCOM(Force.of(Vector.sumOf(movingFromGreater, movingFromLower)),
                "Extracted from " + greater.description() + " and " + lower.description());
    }

    private void addColliderAcceleration(List<BodyAcceleration> accelerations, C calculations) {
        var pushingDirection = calculations.getModel().getState().getPushingDirection();
        var rotatingDirection = calculations.getModel().getState().getRotatingDirection();
        if (pushingDirection != null) {
            switch (pushingDirection) {
                case UP -> accelerations.add(new BodyAcceleration().setY(COLLIDER_PUSHING));
                case DOWN -> accelerations.add(new BodyAcceleration().setY(-COLLIDER_PUSHING));
                case LEFT -> accelerations.add(new BodyAcceleration().setX(-COLLIDER_PUSHING));
                case RIGHT -> accelerations.add(new BodyAcceleration().setX(COLLIDER_PUSHING));
            }
        }
        if (rotatingDirection != null) {
            switch (rotatingDirection) {
                case RIGHT -> accelerations.add(new BodyAcceleration().setAngle(-COLLIDER_ROTATING));
                case LEFT -> accelerations.add(new BodyAcceleration().setAngle(COLLIDER_ROTATING));
            }
        }
    }
}
