package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.MissileAccelerations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Acceleration;
import com.github.aadvorak.artilleryonline.battle.common.BodyAcceleration;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

public class MissileAccelerationCalculator {

    public static BodyAcceleration calculate(MissileCalculations calculations, BattleModel battleModel) {
        var accelerations = new MissileAccelerations();

        accelerations.getGravity()
                .setY(-battleModel.getRoom().getSpecs().getGravityAcceleration());

        accelerations.setPushing(calculatePushing(calculations));
        // todo
        return accelerations.sum();
    }

    private static Acceleration calculatePushing(MissileCalculations calculations) {
        var angle = calculations.getModel().getState().getPosition().getAngle();
        var pushingMagnitude = calculations.getModel().getSpecs().getPushingAcceleration();
        return new Acceleration()
                .setX(pushingMagnitude * Math.cos(angle))
                .setY(pushingMagnitude * Math.sin(angle));
    }
}
