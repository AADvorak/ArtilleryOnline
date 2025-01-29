package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.MissileAccelerations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.missile.CorrectingAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.common.Acceleration;
import com.github.aadvorak.artilleryonline.battle.common.BodyAcceleration;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

public class MissileAccelerationCalculator {

    public static BodyAcceleration calculate(MissileCalculations calculations, BattleModel battleModel) {
        var accelerations = new MissileAccelerations();

        accelerations.getGravity()
                .setY(-battleModel.getRoom().getSpecs().getGravityAcceleration());
        accelerations.setFriction(calculateFriction(calculations,
                battleModel.getRoom().getSpecs().getAirFrictionCoefficient()));
        accelerations.setPushing(calculatePushing(calculations));
        accelerations.setCorrecting(CorrectingAccelerationCalculator.calculate(calculations, battleModel));
        // todo returning
        return accelerations.sum();
    }

    private static Acceleration calculatePushing(MissileCalculations calculations) {
        var angle = calculations.getModel().getState().getPosition().getAngle();
        var pushingMagnitude = calculations.getModel().getSpecs().getPushingAcceleration();
        return new Acceleration()
                .setX(pushingMagnitude * Math.cos(angle))
                .setY(pushingMagnitude * Math.sin(angle));
    }

    private static BodyAcceleration calculateFriction(MissileCalculations calculations, double frictionCoefficient) {
        var velocity = calculations.getModel().getState().getVelocity();
        var positionAngle = calculations.getModel().getState().getPosition().getAngle();
        var velocityAngle = calculations.getModel().getState().getVelocity().getMovingVelocity().angle();
        var diffAngle = positionAngle - velocityAngle;
        var resultCoefficient = frictionCoefficient * (1 + Math.abs(Math.sin(diffAngle)));
        return new BodyAcceleration()
                .setX( - velocity.getX() * Math.abs(velocity.getX()) * resultCoefficient)
                .setY( - velocity.getY() * Math.abs(velocity.getY()) * resultCoefficient)
                .setAngle(- velocity.getAngle());
    }
}
