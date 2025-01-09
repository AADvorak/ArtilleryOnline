package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;

public class VehicleWallCollisionsProcessor {

    public static void process(VehicleCalculations vehicle, BattleCalculations battle) {
        if (wallCollide(vehicle, battle)) {
            resolve(vehicle, battle);
            vehicle.getModel().setUpdated(true);
            vehicle.getCollisions().add(new Collision()
                    .setType(CollideObjectType.WALL));
            vehicle.setHasCollisions(true);
        }
    }

    public static boolean checkResolved(VehicleCalculations vehicle, BattleCalculations battle) {
        return !wallCollide(vehicle, battle);
    }

    private static boolean wallCollide(VehicleCalculations vehicle, BattleCalculations battle) {
        var velocityX = vehicle.getModel().getState().getVelocity().getX();
        var wheelRadius = vehicle.getModel().getSpecs().getWheelRadius();
        var xMax = battle.getModel().getRoom().getSpecs().getRightTop().getX();
        var xMin = battle.getModel().getRoom().getSpecs().getLeftBottom().getX();
        if (velocityX > 0) {
            var rightWheelPosition = vehicle.getRightWheel().getNext().getPosition();
            return rightWheelPosition.getX() + wheelRadius >= xMax;
        }
        if (velocityX < 0) {
            var leftWheelPosition = vehicle.getLeftWheel().getNext().getPosition();
            return leftWheelPosition.getX() - wheelRadius <= xMin;
        }
        return false;
    }

    private static void resolve(VehicleCalculations vehicle, BattleCalculations battle) {
        vehicle.getModel().getState().getVelocity().setX(
                - vehicle.getModel().getState().getVelocity().getX() / 2);
        vehicle.calculateNextPositionAndAngle(battle.getModel().getCurrentTimeStepSecs());
    }
}
