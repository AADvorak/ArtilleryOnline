package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.BodyCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ForceAtPoint;
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

    private final List<ForceCalculator<S, P, Cf, St, M, C>> forceCalculators;

    public BodyAcceleration calculate(C calculations, BattleModel battleModel) {
        List<ForceAtPoint> forces = new ArrayList<>();
        List<BodyAcceleration> accelerations = new ArrayList<>();
        forceCalculators.forEach(forceCalculator ->
                forces.addAll(forceCalculator.calculate(calculations, battleModel)));
        forces.forEach(force -> accelerations.add(toAcceleration(force, calculations)));
        return BodyAcceleration.sumOf(accelerations);
    }

    private BodyAcceleration toAcceleration(ForceAtPoint force, C calculations) {
        var mass = calculations.getMass();
        var acceleration = new BodyAcceleration()
                .setX(force.force().getX() / mass)
                .setY(force.force().getY() / mass);
        if (force.point() == null) {
            return acceleration;
        }
        var centerOfMass = calculations.getPosition();
        var radiusVector = centerOfMass.vectorTo(force.point());
        var momentOfInertia = calculations.getModel().getPreCalc().getMomentOfInertia();
        var torque = radiusVector.vectorProduct(force.force());
        acceleration.setAngle(torque / momentOfInertia);
        return acceleration;
    }
}
