package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.utils.VectorUtils;

public class VehicleCollisionsProcessor {

    public static void process(VehicleCalculations vehicle, BattleCalculations battle) {
        var collision = VehicleCollisionsDetector.detectFirst(vehicle, battle);
        if (collision != null) {
            vehicle.setHasCollisions(true);
            if (collisionAlreadyProcessed(vehicle, collision)) {
                return;
            }
            resolve(collision, battle);
            vehicle.getModel().setUpdated(true);
            vehicle.getCollisions().add(collision);
        }
    }

    public static boolean checkResolved(VehicleCalculations vehicle, BattleCalculations battle) {
        var collision = VehicleCollisionsDetector.detectFirst(vehicle, battle);
        return collision == null;
    }

    // todo do this check while collisions detection
    private static boolean collisionAlreadyProcessed(VehicleCalculations vehicle, Collision collision) {
        return collision.getPair().second().getCollisions().stream()
                .anyMatch(c -> CollideObjectType.VEHICLE.equals(c.getType())
                        && c.getVehicleId().equals(vehicle.getModel().getId()));
    }

    private static void resolve(Collision collision, BattleCalculations battle) {
        recalculateVehiclesVelocities(collision);
        collision.getPair().first().getVehicleCalculations()
                .calculateNextPositionAndAngle(battle.getModel().getCurrentTimeStepSecs());
        collision.getPair().second().getVehicleCalculations()
                .calculateNextPositionAndAngle(battle.getModel().getCurrentTimeStepSecs());
        recalculateVehiclesNextPositions(collision);
        collision.getPair().second().getCollisions().add(Collision.inverted(collision));
        collision.getPair().second().getModel().setUpdated(true);
        collision.getPair().second().getVehicleCalculations().setHasCollisions(true);
    }

    private static void recalculateVehiclesVelocities(Collision collision) {
        var mass = collision.getPair().first().getModel().getPreCalc().getMass();
        var otherMass = collision.getPair().second().getModel().getPreCalc().getMass();

        if (collision.getPair().first() instanceof WheelCalculations wheelCalculations) {
            wheelCalculations.getVehicle().recalculateWheelsVelocities();
        }
        if (collision.getPair().second() instanceof WheelCalculations wheelCalculations) {
            wheelCalculations.getVehicle().recalculateWheelsVelocities();
        }

        var velocity = collision.getPair().first().getVelocity();
        var otherVelocity = collision.getPair().second().getVelocity();

        var velocityVerticalProjection = VectorUtils.getVerticalProjection(velocity, collision.getAngle());
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(velocity, collision.getAngle());
        var otherVelocityVerticalProjection = VectorUtils.getVerticalProjection(otherVelocity, collision.getAngle());

        var newVelocityVerticalProjection = getNewVelocityVerticalProjection(
                velocityVerticalProjection, otherVelocityVerticalProjection,
                mass, otherMass);
        collision.getPair().first().setVelocity(
                new Velocity()
                        .setX(VectorUtils.getComponentX(newVelocityVerticalProjection, velocityHorizontalProjection, collision.getAngle()))
                        .setY(VectorUtils.getComponentY(newVelocityVerticalProjection, velocityHorizontalProjection, collision.getAngle()))
        );

        var otherVelocityHorizontalProjection = VectorUtils.getHorizontalProjection(otherVelocity, collision.getAngle());
        var otherNewVelocityVerticalProjection = getNewVelocityVerticalProjection(
                otherVelocityVerticalProjection, velocityVerticalProjection,
                otherMass, mass);
        collision.getPair().second().setVelocity(
                new Velocity()
                        .setX(VectorUtils.getComponentX(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collision.getAngle()))
                        .setY(VectorUtils.getComponentY(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collision.getAngle()))
        );

        if (collision.getPair().first() instanceof WheelCalculations wheelCalculations) {
            wheelCalculations.getVehicle().recalculateVelocityByWheel(wheelCalculations);
        }
        if (collision.getPair().second() instanceof WheelCalculations wheelCalculations) {
            wheelCalculations.getVehicle().recalculateVelocityByWheel(wheelCalculations);
        }
    }

    private static void recalculateVehiclesNextPositions(Collision collision) {
        var vehicle = collision.getPair().first().getVehicleCalculations();
        var otherVehicle = collision.getPair().second().getVehicleCalculations();
        var mass = collision.getPair().first().getModel().getPreCalc().getMass();
        var otherMass = collision.getPair().second().getModel().getPreCalc().getMass();
        var normalMovePerMass = collision.getInterpenetration() / (mass + otherMass);
        var normalMove = normalMovePerMass * mass;
        var otherNormalMove = normalMovePerMass * otherMass;
        vehicle.applyNormalMoveToNextPosition(normalMove, collision.getAngle());
        otherVehicle.applyNormalMoveToNextPosition(- otherNormalMove, collision.getAngle());
    }

    private static double getNewVelocityVerticalProjection(
            double velocityVerticalProjection, double otherVelocityVerticalProjection,
            double mass, double otherMass
    ) {
        return (- Math.abs(mass - otherMass) * velocityVerticalProjection
                + 2 * otherMass * otherVelocityVerticalProjection) / (mass + otherMass);
    }
}
