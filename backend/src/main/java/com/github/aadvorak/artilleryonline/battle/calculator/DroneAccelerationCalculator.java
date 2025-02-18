package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Acceleration;
import com.github.aadvorak.artilleryonline.battle.common.BodyAcceleration;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class DroneAccelerationCalculator {

    public static BodyAcceleration calculate(DroneCalculations drone, BattleModel battleModel) {
        var gravity = new BodyAcceleration()
                .setY(-battleModel.getRoom().getSpecs().getGravityAcceleration());
        var friction = calculateFriction(drone, battleModel.getRoom().getSpecs().getAirFrictionCoefficient());
        var engines = calculateEngines(drone, battleModel);
        return BodyAcceleration.sumOf(gravity, friction, engines);
    }

    private static BodyAcceleration calculateFriction(DroneCalculations drone, double frictionCoefficient) {
        var velocity = drone.getModel().getState().getVelocity();
        return new BodyAcceleration()
                .setX( - velocity.getX() * Math.abs(velocity.getX()) * frictionCoefficient)
                .setY( - velocity.getY() * Math.abs(velocity.getY()) * frictionCoefficient)
                .setAngle(- velocity.getAngle());
    }

    private static BodyAcceleration calculateEngines(DroneCalculations drone, BattleModel battleModel) {
        // average acceleration depends on height over ground and velocity
        // may be on closing objects
        // difference of engines acceleration depends on angle and moving direction
        var angle = drone.getModel().getState().getPosition().getAngle();
        var accelerationMagnitude = getEngineAccelerationMagnitude(drone, battleModel);
        var rightEngineAccelerationMagnitude = accelerationMagnitude / 2;
        var leftEngineAccelerationMagnitude = accelerationMagnitude / 2;
        var rightEngineAcceleration = new Acceleration()
                .setX(rightEngineAccelerationMagnitude * Math.cos(angle))
                .setY(rightEngineAccelerationMagnitude * Math.sin(angle));
        var leftEngineAcceleration = new Acceleration()
                .setX(leftEngineAccelerationMagnitude * Math.cos(angle))
                .setY(leftEngineAccelerationMagnitude * Math.sin(angle));
        var rightEngineRotatingAcceleration = rightEngineAcceleration.getX() * Math.sin(angle)
                + rightEngineAcceleration.getY() * Math.cos(angle);
        var leftEngineRotatingAcceleration = leftEngineAcceleration.getX() * Math.sin(angle)
                + leftEngineAcceleration.getY() * Math.cos(angle);
        var engineRadius = drone.getModel().getSpecs().getEnginesRadius();
        return new BodyAcceleration()
                .setX((rightEngineAcceleration.getX() + leftEngineAcceleration.getX()) / 2)
                .setY((rightEngineAcceleration.getY() + leftEngineAcceleration.getY()) / 2)
                .setAngle((rightEngineRotatingAcceleration - leftEngineRotatingAcceleration) / (2 * engineRadius));
    }

    private static double getHeight(DroneCalculations drone, BattleModel battleModel) {
        var position = drone.getModel().getState().getPosition().getCenter();
        var nearestGroundPosition = BattleUtils.getNearestGroundPosition(position.getX(), battleModel.getRoom());
        return position.getY() - nearestGroundPosition.getY();
    }

    private static double getEngineAccelerationMagnitude(DroneCalculations drone, BattleModel battleModel) {
        var flyHeight = drone.getModel().getSpecs().getFlyHeight();
        var currentHeight = getHeight(drone, battleModel);
        if (currentHeight > flyHeight) {
            return 0.0;
        } else {
            return drone.getModel().getSpecs().getMaxEngineAcceleration();
        }
    }
}
