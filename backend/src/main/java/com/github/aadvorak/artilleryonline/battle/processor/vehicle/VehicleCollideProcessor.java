package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.CollideObject;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.InterpenetrationUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VectorUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

import java.util.stream.Collectors;

public class VehicleCollideProcessor {

    public static boolean processCollide(VehicleCalculations vehicle, BattleCalculations battle) {
        var collisionData = vehicleCollide(vehicle, battle);
        if (collisionData != null) {
            doCollide(vehicle, collisionData);
            vehicle.getModel().setUpdated(true);
            vehicle.getCollisions().add(new CollideObject()
                    .setType(CollideObjectType.VEHICLE)
                    .setVehicleId(collisionData.otherVehicle().getModel().getId()));
            return true;
        }
        return false;
    }

    public static boolean checkResolved(VehicleCalculations vehicle, BattleCalculations battle) {
        var collisionData = vehicleCollide(vehicle, battle);
        return collisionData == null;
    }

    private static CollisionData vehicleCollide(VehicleCalculations vehicle, BattleCalculations battle) {
        var otherVehicles = battle.getVehicles().stream()
                .filter(value -> value.getModel().getId() != vehicle.getModel().getId())
                .collect(Collectors.toSet());
        var wheelRadius = vehicle.getModel().getSpecs().getWheelRadius();
        var vehicleRadius = vehicle.getModel().getSpecs().getRadius();
        var position = vehicle.getNextPosition();
        var angle = vehicle.getNextAngle();
        var rightWheelPosition = VehicleUtils.getNextRightWheelPosition(vehicle);
        var leftWheelPosition = VehicleUtils.getNextLeftWheelPosition(vehicle);
        for (var otherVehicle : otherVehicles) {
            var otherPosition = otherVehicle.getNextPosition();
            var otherAngle = otherVehicle.getNextAngle();
            var otherVehicleRadius = otherVehicle.getModel().getSpecs().getRadius();
            var vehicleVehicleInterpenetration = InterpenetrationUtils.getVehicleVehicleInterpenetration(position,
                    otherPosition, angle, otherAngle, vehicleRadius, otherVehicleRadius);
            if (vehicleVehicleInterpenetration > 0) {
                return new CollisionData(otherVehicle, null, null, vehicleVehicleInterpenetration);
            }
            var otherWheelRadius = otherVehicle.getModel().getSpecs().getWheelRadius();
            var otherLeftWheelPosition = VehicleUtils.getNextLeftWheelPosition(otherVehicle);
            var otherRightWheelPosition = VehicleUtils.getNextRightWheelPosition(otherVehicle);
            var otherLeftWheel = otherVehicle.getLeftWheel();
            var otherRightWheel = otherVehicle.getRightWheel();
            var rightWheelLeftWheelInterpenetration = InterpenetrationUtils.getWheelWheelInterpenetration(rightWheelPosition,
                    otherLeftWheelPosition, wheelRadius, otherWheelRadius);
            if (rightWheelLeftWheelInterpenetration > 0) {
                return new CollisionData(otherVehicle, vehicle.getRightWheel(), otherLeftWheel,
                        rightWheelLeftWheelInterpenetration);
            }
            var leftWheelRightWheelInterpenetration = InterpenetrationUtils.getWheelWheelInterpenetration(leftWheelPosition,
                    otherRightWheelPosition, wheelRadius, otherWheelRadius);
            if (leftWheelRightWheelInterpenetration > 0) {
                return new CollisionData(otherVehicle, vehicle.getLeftWheel(), otherRightWheel,
                        leftWheelRightWheelInterpenetration);
            }
            var rightWheelVehicleInterpenetration = InterpenetrationUtils.getWheelVehicleInterpenetration(rightWheelPosition,
                    otherPosition, otherAngle, wheelRadius, otherVehicleRadius);
            if (rightWheelVehicleInterpenetration > 0) {
                return new CollisionData(otherVehicle, vehicle.getRightWheel(), null,
                        rightWheelVehicleInterpenetration);
            }
            var vehicleRightWheelInterpenetration = InterpenetrationUtils.getWheelVehicleInterpenetration(
                    otherRightWheelPosition, position, angle, otherWheelRadius, vehicleRadius);
            if (vehicleRightWheelInterpenetration > 0) {
                return new CollisionData(otherVehicle, null, otherRightWheel,
                        vehicleRightWheelInterpenetration);
            }
            var vehicleLeftWheelInterpenetration = InterpenetrationUtils.getWheelVehicleInterpenetration(otherLeftWheelPosition,
                    position, angle, otherWheelRadius, vehicleRadius);
            if (vehicleLeftWheelInterpenetration > 0) {
                return new CollisionData(otherVehicle, null, otherLeftWheel, vehicleLeftWheelInterpenetration);
            }
            var leftWheelVehicleInterpenetration = InterpenetrationUtils.getWheelVehicleInterpenetration(leftWheelPosition,
                    otherPosition, otherAngle, wheelRadius, otherVehicleRadius);
            if (leftWheelVehicleInterpenetration > 0) {
                return new CollisionData(otherVehicle, vehicle.getLeftWheel(), null,
                        leftWheelVehicleInterpenetration);
            }
            var leftWheelLeftWheelInterpenetration = InterpenetrationUtils.getWheelWheelInterpenetration(leftWheelPosition,
                    otherLeftWheelPosition, wheelRadius, otherWheelRadius);
            if (leftWheelLeftWheelInterpenetration > 0) {
                return new CollisionData(otherVehicle, vehicle.getLeftWheel(), otherLeftWheel,
                        leftWheelLeftWheelInterpenetration);
            }
            var rightWheelRightWheelInterpenetration = InterpenetrationUtils.getWheelWheelInterpenetration(rightWheelPosition,
                    otherRightWheelPosition, wheelRadius, otherWheelRadius);
            if (rightWheelRightWheelInterpenetration > 0) {
                return new CollisionData(otherVehicle, vehicle.getRightWheel(), otherRightWheel,
                        rightWheelRightWheelInterpenetration);
            }
        }
        return null;
    }

    private static void doCollide(VehicleCalculations vehicle, CollisionData collisionData) {
        if (collisionData.wheel() == null && collisionData.otherWheel() == null) {
            doCollideVehicleVehicle(vehicle.getModel(), collisionData);
        }
        if (collisionData.wheel() != null && collisionData.otherWheel() != null) {
            doCollideWheelWheel(vehicle, collisionData);
        }
        if (collisionData.wheel() != null && collisionData.otherWheel() == null) {
            doCollideWheelVehicle(vehicle, collisionData);
        }
        if (collisionData.wheel() == null && collisionData.otherWheel() != null) {
            doCollideVehicleWheel(vehicle.getModel(), collisionData);
        }
        collisionData.otherVehicle().getCollisions().add(new CollideObject()
                .setType(CollideObjectType.VEHICLE)
                .setVehicleId(vehicle.getModel().getId()));
        collisionData.otherVehicle().getModel().setUpdated(true);
    }

    private static void doCollideVehicleVehicle(VehicleModel vehicle, CollisionData collisionData) {
        var otherVehicle = collisionData.otherVehicle().getModel();
        var collisionAngle = getCollisionAngle(vehicle.getState().getPosition(), otherVehicle.getState().getPosition());
        var mass = vehicle.getPreCalc().getMass();
        var otherMass = otherVehicle.getPreCalc().getMass();

        var velocity = vehicle.getState().getVelocity().getMovingVelocity();
        var otherVelocity = otherVehicle.getState().getVelocity().getMovingVelocity();

        var velocityVerticalProjection = VectorUtils.getVerticalProjection(velocity, collisionAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(velocity, collisionAngle);
        var otherVelocityVerticalProjection = VectorUtils.getVerticalProjection(otherVelocity, collisionAngle);

        var newVelocityVerticalProjection = getNewVelocityVerticalProjection(
                velocityVerticalProjection, otherVelocityVerticalProjection,
                mass, otherMass);
        vehicle.getState().getVelocity()
                .setX(VectorUtils.getComponentX(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle));

        var otherVelocityHorizontalProjection = VectorUtils.getHorizontalProjection(otherVelocity, collisionAngle);
        var otherNewVelocityVerticalProjection = getNewVelocityVerticalProjection(
                otherVelocityVerticalProjection, velocityVerticalProjection,
                otherMass, mass);
        otherVehicle.getState().getVelocity()
                .setX(VectorUtils.getComponentX(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle));

        recalculateVehiclesPositions(vehicle, otherVehicle, mass, otherMass, collisionAngle, collisionData.interpenetration());
    }

    private static void doCollideWheelWheel(VehicleCalculations vehicle, CollisionData collisionData) {
        var collisionAngle = getCollisionAngle(collisionData.wheel().getPosition(), collisionData.otherWheel().getPosition());
        var mass = vehicle.getModel().getPreCalc().getMass();
        var otherMass = collisionData.otherVehicle().getModel().getPreCalc().getMass();

        VehicleUtils.calculateWheelVelocity(vehicle.getModel(), vehicle.getRightWheel());
        VehicleUtils.calculateWheelVelocity(vehicle.getModel(), vehicle.getLeftWheel());
        VehicleUtils.calculateWheelVelocity(collisionData.otherVehicle().getModel(), collisionData.otherVehicle().getRightWheel());
        VehicleUtils.calculateWheelVelocity(collisionData.otherVehicle().getModel(), collisionData.otherVehicle().getLeftWheel());

        var velocityVerticalProjection = VectorUtils.getVerticalProjection(collisionData.wheel().getVelocity(), collisionAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(collisionData.wheel().getVelocity(), collisionAngle);
        var otherVelocityVerticalProjection = VectorUtils.getVerticalProjection(collisionData.otherWheel().getVelocity(), collisionAngle);

        var newVelocityVerticalProjection = getNewVelocityVerticalProjection(
                velocityVerticalProjection, otherVelocityVerticalProjection,
                mass, otherMass);
        collisionData.wheel().getVelocity()
                .setX(VectorUtils.getComponentX(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle));

        VehicleUtils.recalculateVehicleVelocityByWheel(vehicle, collisionData.wheel());

        var otherVelocityHorizontalProjection = VectorUtils.getHorizontalProjection(collisionData.otherWheel().getVelocity(), collisionAngle);
        var otherNewVelocityVerticalProjection = getNewVelocityVerticalProjection(
                otherVelocityVerticalProjection, velocityVerticalProjection,
                otherMass, mass);
        collisionData.otherWheel().getVelocity()
                .setX(VectorUtils.getComponentX(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle));

        VehicleUtils.recalculateVehicleVelocityByWheel(collisionData.otherVehicle(), collisionData.otherWheel());

        recalculateVehiclesPositions(vehicle.getModel(), collisionData.otherVehicle().getModel(), mass, otherMass,
                collisionAngle, collisionData.interpenetration());
    }

    private static void doCollideWheelVehicle(VehicleCalculations vehicle, CollisionData collisionData) {
        var collisionAngle = getCollisionAngle(collisionData.wheel().getPosition(),
                collisionData.otherVehicle().getModel().getState().getPosition());
        var otherVelocity = collisionData.otherVehicle().getModel().getState().getVelocity().getMovingVelocity();
        var mass = vehicle.getModel().getPreCalc().getMass();
        var otherMass = collisionData.otherVehicle().getModel().getPreCalc().getMass();

        VehicleUtils.calculateWheelVelocity(vehicle.getModel(), vehicle.getRightWheel());
        VehicleUtils.calculateWheelVelocity(vehicle.getModel(), vehicle.getLeftWheel());

        var velocityVerticalProjection = VectorUtils.getVerticalProjection(collisionData.wheel().getVelocity(), collisionAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(collisionData.wheel().getVelocity(), collisionAngle);
        var otherVelocityVerticalProjection = VectorUtils.getVerticalProjection(otherVelocity, collisionAngle);

        var newVelocityVerticalProjection = getNewVelocityVerticalProjection(
                velocityVerticalProjection, otherVelocityVerticalProjection,
                mass, otherMass);
        collisionData.wheel().getVelocity()
                .setX(VectorUtils.getComponentX(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle));

        VehicleUtils.recalculateVehicleVelocityByWheel(vehicle, collisionData.wheel());

        var otherVelocityHorizontalProjection = VectorUtils.getHorizontalProjection(otherVelocity, collisionAngle);
        var otherNewVelocityVerticalProjection = getNewVelocityVerticalProjection(
                otherVelocityVerticalProjection, velocityVerticalProjection,
                otherMass, mass);
        collisionData.otherVehicle().getModel().getState().getVelocity()
                .setX(VectorUtils.getComponentX(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle));

        recalculateVehiclesPositions(vehicle.getModel(), collisionData.otherVehicle().getModel(), mass, otherMass,
                collisionAngle, collisionData.interpenetration());
    }

    private static void doCollideVehicleWheel(VehicleModel vehicle, CollisionData collisionData) {
        var collisionAngle = getCollisionAngle(vehicle.getState().getPosition(), collisionData.otherWheel().getPosition());
        var velocity = vehicle.getState().getVelocity().getMovingVelocity();
        var mass = vehicle.getPreCalc().getMass();
        var otherMass = collisionData.otherVehicle().getModel().getPreCalc().getMass();

        VehicleUtils.calculateWheelVelocity(collisionData.otherVehicle().getModel(), collisionData.otherVehicle().getRightWheel());
        VehicleUtils.calculateWheelVelocity(collisionData.otherVehicle().getModel(), collisionData.otherVehicle().getLeftWheel());

        var velocityVerticalProjection = VectorUtils.getVerticalProjection(velocity, collisionAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(velocity, collisionAngle);
        var otherVelocityVerticalProjection = VectorUtils.getVerticalProjection(collisionData.otherWheel().getVelocity(), collisionAngle);

        var newVelocityVerticalProjection = getNewVelocityVerticalProjection(
                velocityVerticalProjection, otherVelocityVerticalProjection,
                mass, otherMass);
        vehicle.getState().getVelocity()
                .setX(VectorUtils.getComponentX(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle));

        var otherVelocityHorizontalProjection = VectorUtils.getHorizontalProjection(collisionData.otherWheel().getVelocity(), collisionAngle);
        var otherNewVelocityVerticalProjection = getNewVelocityVerticalProjection(
                otherVelocityVerticalProjection, velocityVerticalProjection,
                otherMass, mass);
        collisionData.otherWheel().getVelocity()
                .setX(VectorUtils.getComponentX(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle));

        VehicleUtils.recalculateVehicleVelocityByWheel(collisionData.otherVehicle(), collisionData.otherWheel());

        recalculateVehiclesPositions(vehicle, collisionData.otherVehicle().getModel(), mass, otherMass,
                collisionAngle, collisionData.interpenetration());
    }

    private static void recalculateVehiclesPositions(
            VehicleModel vehicle, VehicleModel otherVehicle,
            double mass, double otherMass,
            double collisionAngle, double interpenetration
    ) {
        var normalMovePerMass = interpenetration / (mass + otherMass);
        var normalMove = normalMovePerMass * mass;
        var otherNormalMove = normalMovePerMass * otherMass;
        var position = vehicle.getState().getPosition();
        var normalProjection = VectorUtils.getVerticalProjection(position, collisionAngle) + normalMove;
        var tangentialProjection = VectorUtils.getHorizontalProjection(position, collisionAngle);
        position.setX(VectorUtils.getComponentX(normalProjection, tangentialProjection, collisionAngle));
        position.setY(VectorUtils.getComponentY(normalProjection, tangentialProjection, collisionAngle));
        var otherPosition = otherVehicle.getState().getPosition();
        var otherNormalProjection = VectorUtils.getVerticalProjection(otherPosition, collisionAngle) - otherNormalMove;
        var otherTangentialProjection = VectorUtils.getHorizontalProjection(otherPosition, collisionAngle);
        otherPosition.setX(VectorUtils.getComponentX(otherNormalProjection, otherTangentialProjection, collisionAngle));
        otherPosition.setY(VectorUtils.getComponentY(otherNormalProjection, otherTangentialProjection, collisionAngle));
    }

    private static double getCollisionAngle(Position position, Position otherPosition) {
        return Math.asin((position.getY() - otherPosition.getY())
                / position.distanceTo(otherPosition)) - Math.PI / 2;
    }

    private static double getNewVelocityVerticalProjection(
            double velocityVerticalProjection, double otherVelocityVerticalProjection,
            double mass, double otherMass
    ) {
        return (- Math.abs(mass - otherMass) * velocityVerticalProjection
                + 2 * otherMass * otherVelocityVerticalProjection) / (mass + otherMass);
    }

    private record CollisionData(
            VehicleCalculations otherVehicle,
            WheelCalculations wheel,
            WheelCalculations otherWheel,
            double interpenetration
    ) {
    }
}
