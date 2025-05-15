package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.BodyCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.BodyForce;
import com.github.aadvorak.artilleryonline.battle.calculations.ForceCalculator;
import com.github.aadvorak.artilleryonline.battle.common.BodyAcceleration;
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

    private final List<ForceCalculator<S, P, Cf, St, M, C>> forceCalculators;

    public BodyAcceleration calculate(C calculations, BattleModel battleModel) {
        List<BodyForce> forces = new ArrayList<>();
        List<BodyAcceleration> accelerations = new ArrayList<>();
        forceCalculators.forEach(forceCalculator ->
                forces.addAll(forceCalculator.calculate(calculations, battleModel)));
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
            var torque = force.radiusVector().vectorProduct(force.rotating());
            acceleration.setAngle(torque / momentOfInertia);
        }
        return acceleration;
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
