package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Acceleration;
import com.github.aadvorak.artilleryonline.battle.common.BodyAcceleration;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.GeometryUtils;

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
        var maxAccelerationMagnitude = drone.getModel().getSpecs().getMaxEngineAcceleration();
        var accelerationMagnitude = getEnginesAccelerationMagnitude(drone, battleModel);
        var accelerationDiff = getEnginesAccelerationDiff(drone, battleModel);
        var rightEngineAccelerationMagnitude = restricted(accelerationMagnitude * (1 + accelerationDiff), maxAccelerationMagnitude);
        var leftEngineAccelerationMagnitude = restricted(accelerationMagnitude * (1 - accelerationDiff), maxAccelerationMagnitude);
        var rightEngineAcceleration = new Acceleration()
                .setX(rightEngineAccelerationMagnitude * Math.cos(angle + Math.PI / 2))
                .setY(rightEngineAccelerationMagnitude * Math.sin(angle + Math.PI / 2));
        var leftEngineAcceleration = new Acceleration()
                .setX(leftEngineAccelerationMagnitude * Math.cos(angle + Math.PI / 2))
                .setY(leftEngineAccelerationMagnitude * Math.sin(angle + Math.PI / 2));
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

    private static double getEnginesAccelerationMagnitude(DroneCalculations drone, BattleModel battleModel) {
        var flyHeight = drone.getModel().getSpecs().getFlyHeight();
        var currentHeight = getHeight(drone, battleModel);
        var maxAcceleration = drone.getModel().getSpecs().getMaxEngineAcceleration();
        if (currentHeight > 1.5 * flyHeight) {
            return 0.0;
        } else if (currentHeight < 0.5 * flyHeight) {
            return maxAcceleration;
        } else {
            var velocityY = drone.getModel().getState().getVelocity().getY();
            if (velocityY < -3.0) {
                return maxAcceleration;
            }
            var distance = flyHeight - currentHeight;
            var timeStep = battleModel.getCurrentTimeStepSecs();
            var gravityAcceleration = battleModel.getRoom().getSpecs().getGravityAcceleration();
            var absVelocityY = Math.abs(velocityY);
            var absDistance = Math.abs(distance);
            if (absVelocityY < 0.1 && absDistance < 0.01) {
                return gravityAcceleration;
            }
            var targetAcceleration = absVelocityY < 0.1
                    ? gravityAcceleration + 2 * distance / (timeStep * timeStep)
                    : gravityAcceleration - Math.signum(velocityY) * velocityY * velocityY / (2 * distance);
            if (targetAcceleration < 0) {
                return 0.0;
            }
            return Math.min(targetAcceleration, maxAcceleration);
        }
    }

    private static double getEnginesAccelerationDiff(DroneCalculations drone, BattleModel battleModel) {
        var targetAngle = getTargetAngle(drone, battleModel);
        var droneAngle = drone.getModel().getState().getPosition().getAngle();
        var angleDiff = GeometryUtils.calculateAngleDiff(droneAngle, targetAngle);
        if (angleDiff > Math.PI) {
            return 2.0;
        } else if (angleDiff < -Math.PI) {
            return -2.0;
        } else {
            return 2 * angleDiff / Math.PI;
        }
    }

    private static double getTargetAngle(DroneCalculations drone, BattleModel battleModel) {
        var flyHeight = drone.getModel().getSpecs().getFlyHeight();
        var currentHeight = getHeight(drone, battleModel);
        if (currentHeight < 0.8 * flyHeight) {
            return 0.0;
        }
        if (drone.getTarget() == null) {
            return 0.0;
        }
        if (Math.abs(drone.getTarget().getXDiff()) < 1.5) {
            return 0.0;
        }
        return - Math.signum(drone.getTarget().getXDiff()) * Math.PI / 16;
    }

    private static double restricted(double value, double maxValue) {
        if (value > maxValue) {
            return maxValue;
        }
        if (value < -maxValue) {
            return -maxValue;
        }
        return value;
    }
}
