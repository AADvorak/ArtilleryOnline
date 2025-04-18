package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.BodyCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ForceAtPoint;
import com.github.aadvorak.artilleryonline.battle.calculations.ForceCalculator;
import com.github.aadvorak.artilleryonline.battle.common.BodyAcceleration;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BodyAccelerationCalculator<C extends BodyCalculations> {

    private final List<ForceCalculator<C>> forceCalculators;

    public BodyAcceleration calculate(C calculations, BattleModel battleModel) {
        List<ForceAtPoint> forces = new ArrayList<>();
        List<BodyAcceleration> accelerations = new ArrayList<>();
        forceCalculators.forEach(forceCalculator ->
                forces.add(forceCalculator.calculate(calculations, battleModel)));
        forces.forEach(force -> accelerations.add(toAcceleration(force, calculations)));
        return BodyAcceleration.sumOf(accelerations);
    }

    private BodyAcceleration toAcceleration(ForceAtPoint force, C calculations) {
        var mass = calculations.getMass();
        var acceleration = new BodyAcceleration();
        if (force.point() == null) {
            return acceleration
                    .setX(force.force().getX() / mass)
                    .setY(force.force().getY() / mass);
        }
        var centerOfMass = calculations.getModel().getCenterOfMass();
        var radiusVector = centerOfMass.vectorTo(force.point());
        var movingForce = force.force().projectionOnto(radiusVector);
        acceleration.setX(movingForce.getX() / mass);
        acceleration.setY(movingForce.getY() / mass);
        var momentOfInertia = calculations.getModel().getPreCalc().getMomentOfInertia();
        var torque = radiusVector.vectorProduct(force.force());
        acceleration.setAngle(torque / momentOfInertia);
        return acceleration;
    }
}
