package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.HalfCircle;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;

import java.util.Set;
import java.util.stream.Collectors;

public class CollisionUtils {

    public static void pushVehicleByDirectHit(Collision collision) {
        var projectileMass = collision.getPair().first().getMass();
        pushVehicleByDirectHit(collision, projectileMass);
    }

    public static void pushVehicleByDirectHit(Collision collision, double projectileMass) {
        var vehicleMass = collision.getPair().second().getMass();
        if (collision.getPair().second() instanceof VehicleCalculations vehicle) {
            var vehicleVelocitiesProjections = VectorProjections.copyOf(collision.getVelocitiesProjections().second());
            var projectileVelocitiesProjections = collision.getVelocitiesProjections().first();
            vehicleVelocitiesProjections.setNormal(vehicleVelocitiesProjections.getNormal()
                    + projectileMass * projectileVelocitiesProjections.getNormal() / vehicleMass);
            vehicle.setVelocity(vehicleVelocitiesProjections.recoverVelocity());

            var vehicleVelocity = vehicle.getModel().getState().getVelocity();
            var vehicleRadius = vehicle.getModel().getSpecs().getRadius();
            vehicleVelocity.setAngle(vehicleVelocity.getAngle()
                    - projectileVelocitiesProjections.getTangential() * (projectileMass / vehicleMass) / vehicleRadius);
        }
        if (collision.getPair().second() instanceof WheelCalculations wheel) {
            var wheelVelocity = wheel.getVelocity();
            var projectileVelocity = collision.getPair().first().getVelocity();
            wheel.setVelocity(new Velocity()
                    .setX(wheelVelocity.getX() + projectileMass * projectileVelocity.getX() / vehicleMass)
                    .setY(wheelVelocity.getY() + projectileMass * projectileVelocity.getY() / vehicleMass));
        }
    }

    // todo common logic
    public static void pushDroneByDirectHit(Collision collision) {
        var drone = (DroneCalculations) collision.getPair().second();
        var droneMass = collision.getPair().second().getMass();
        var projectileMass = collision.getPair().first().getMass();
        var droneVelocitiesProjections = VectorProjections.copyOf(collision.getVelocitiesProjections().second());
        var projectileVelocitiesProjections = collision.getVelocitiesProjections().first();
        droneVelocitiesProjections.setNormal(droneVelocitiesProjections.getNormal()
                + projectileMass * projectileVelocitiesProjections.getNormal() / droneMass);
        drone.setVelocity(droneVelocitiesProjections.recoverVelocity());

        var droneVelocity = drone.getModel().getState().getVelocity();
        var droneRadius = drone.getModel().getSpecs().getHullRadius();
        droneVelocity.setAngle(droneVelocity.getAngle()
                - projectileVelocitiesProjections.getTangential() * (projectileMass / droneMass) / droneRadius);
    }

    public static Collision detectWithMissile(Calculations<?> calculations, Position position, Position nextPosition,
                                              MissileCalculations missile) {
        var missileSegment = new Segment(missile.getPositions().getHead(), missile.getPositions().getTail());
        var projectileTrace = new Segment(position, nextPosition);
        if (GeometryUtils.getSegmentsIntersectionPoint(projectileTrace, missileSegment) != null) {
            var projection = GeometryUtils.getPointToLineProjection(position, missileSegment);
            var interpenetration = Interpenetration.withUncheckedDepth(0.0, position, projection);
            return Collision.withMissile(calculations, missile, interpenetration);
        }
        return null;
    }

    public static Collision detectWithVehicle(Calculations<?> calculations, Position position, Position nextPosition,
                                              VehicleCalculations vehicle) {
        var vehiclePosition = vehicle.getGeometryPosition();
        var vehicleRadius = vehicle.getModel().getSpecs().getRadius();
        var vehicleAngle = vehicle.getModel().getState().getPosition().getAngle();
        var projectileTrace = new Segment(position, nextPosition);
        var vehicleShape = new HalfCircle(vehiclePosition, vehicleRadius, vehicleAngle);
        var intersectionPoints = GeometryUtils.getSegmentAndCircleIntersectionPoints(projectileTrace, vehicleShape.circle())
                .stream().filter(intersectionPoint -> {
                    var pointAngle = GeometryUtils.getPointAngleInCircle(vehiclePosition, intersectionPoint);
                    return pointAngle > vehicleAngle && pointAngle < vehicleAngle + Math.PI;
                }).collect(Collectors.toSet());
        if (!intersectionPoints.isEmpty()) {
            var intersectionPoint = findClosestIntersectionPoint(position, intersectionPoints);
            var interpenetration = Interpenetration.withUncheckedDepth(0.0, intersectionPoint, vehiclePosition);
            return Collision.withVehicle(calculations, vehicle, interpenetration);
        }
        var vehicleBottom = vehicleShape.chord();
        if (GeometryUtils.getSegmentsIntersectionPoint(projectileTrace, vehicleBottom) != null) {
            var projection = GeometryUtils.getPointToLineProjection(position, vehicleBottom);
            var interpenetration = Interpenetration.withUncheckedDepth(0.0, position, projection);
            return Collision.withVehicle(calculations, vehicle, interpenetration);
        }
        return null;
    }

    public static Collision detectWithWheel(Calculations<?> calculations, Position position, Position nextPosition,
                                             WheelCalculations wheel) {
        var wheelPosition = wheel.getPosition();
        var wheelRadius = wheel.getModel().getSpecs().getWheelRadius();
        var projectileTrace = new Segment(position, nextPosition);
        var wheelShape = new Circle(wheelPosition, wheelRadius);
        var intersectionPoints = GeometryUtils.getSegmentAndCircleIntersectionPoints(projectileTrace, wheelShape);
        if (!intersectionPoints.isEmpty()) {
            var intersectionPoint = findClosestIntersectionPoint(position, intersectionPoints);
            var interpenetration = Interpenetration.withUncheckedDepth(0.0, intersectionPoint, wheelPosition);
            return Collision.withVehicle(calculations, wheel, interpenetration);
        }
        return null;
    }

    public static Collision detectWithDrone(Calculations<?> calculations, Position position, Position nextPosition,
                                            DroneCalculations drone) {
        var dronePosition = drone.getPosition();
        var droneRadius = drone.getModel().getSpecs().getHullRadius();
        var projectileTrace = new Segment(position, nextPosition);
        var droneShape = new Circle(dronePosition, droneRadius);
        var intersectionPoints = GeometryUtils.getSegmentAndCircleIntersectionPoints(projectileTrace, droneShape);
        if (!intersectionPoints.isEmpty()) {
            var intersectionPoint = findClosestIntersectionPoint(position, intersectionPoints);
            var interpenetration = Interpenetration.withUncheckedDepth(0.0, intersectionPoint, dronePosition);
            return Collision.withDrone(calculations, drone, interpenetration);
        }

        var enginesRadius = drone.getModel().getSpecs().getEnginesRadius();
        var droneAngle = drone.getModel().getState().getPosition().getAngle();

        var rightEngineCenterPosition = dronePosition.shifted(enginesRadius, droneAngle)
                .shifted(droneRadius, droneAngle + Math.PI / 2);
        var rightEngineShape = new Segment(
                rightEngineCenterPosition.shifted(enginesRadius / 3, droneAngle - Math.PI),
                rightEngineCenterPosition.shifted(enginesRadius / 3, droneAngle)
        );
        var rightEngineIntersectionPoint = GeometryUtils.getSegmentsIntersectionPoint(projectileTrace, rightEngineShape);
        if (rightEngineIntersectionPoint != null) {
            var interpenetration = Interpenetration.withUncheckedDepth(0.0, rightEngineIntersectionPoint, dronePosition);
            return Collision.withDrone(calculations, drone, interpenetration);
        }

        var leftEngineCenterPosition = dronePosition.shifted(enginesRadius, droneAngle - Math.PI)
                .shifted(droneRadius, droneAngle + Math.PI / 2);
        var leftEngineShape = new Segment(
                leftEngineCenterPosition.shifted(enginesRadius / 3, droneAngle - Math.PI),
                leftEngineCenterPosition.shifted(enginesRadius / 3, droneAngle)
        );
        var leftEngineIntersectionPoint = GeometryUtils.getSegmentsIntersectionPoint(projectileTrace, leftEngineShape);
        if (leftEngineIntersectionPoint != null) {
            var interpenetration = Interpenetration.withUncheckedDepth(0.0, leftEngineCenterPosition, dronePosition);
            return Collision.withDrone(calculations, drone, interpenetration);
        }
        return null;
    }

    public static void resolveGroundCollision(Collision collision, BattleCalculations battle) {
        var velocityProjections = VectorProjections.copyOf(collision.getVelocitiesProjections().first());
        velocityProjections.setNormal(-0.5 * velocityProjections.getNormal());
        collision.getPair().first().setVelocity(velocityProjections.recoverVelocity());

        collision.getPair().first().calculateNextPosition(battle.getModel().getCurrentTimeStepSecs());
        collision.getPair().first().applyNormalMoveToNextPosition(collision.getInterpenetration().depth(),
                collision.getInterpenetration().angle());
    }

    public static void resolveRigidCollision(Collision collision, BattleCalculations battle) {
        recalculateVelocitiesRigid(collision);
        collision.getPair().first().calculateNextPosition(battle.getModel().getCurrentTimeStepSecs());
        collision.getPair().second().calculateNextPosition(battle.getModel().getCurrentTimeStepSecs());
        recalculateNextPositionsRigid(collision);
        collision.getPair().second().getCollisions().add(collision.inverted());
    }

    public static void recalculateNextPositionsRigid(Collision collision) {
        var object = collision.getPair().first();
        var otherObject = collision.getPair().second();
        var mass = collision.getPair().first().getMass();
        var otherMass = collision.getPair().second().getMass();
        var normalMovePerMass = collision.getInterpenetration().depth() / (mass + otherMass);
        var normalMove = normalMovePerMass * otherMass;
        var otherNormalMove = normalMovePerMass * mass;
        object.applyNormalMoveToNextPosition(normalMove, collision.getInterpenetration().angle());
        otherObject.applyNormalMoveToNextPosition(- otherNormalMove, collision.getInterpenetration().angle());
    }

    public static void recalculateVelocitiesRigid(Collision collision) {
        recalculateVelocitiesRigid(collision, 1.0);
    }

    public static void recalculateVelocitiesRigid(Collision collision, double coefficient) {
        var mass = collision.getPair().first().getMass();
        var otherMass = collision.getPair().second().getMass();

        var velocityProjections = VectorProjections.copyOf(collision.getVelocitiesProjections().first());
        var otherVelocityProjections = VectorProjections.copyOf(collision.getVelocitiesProjections().second());

        var velocityNormalProjection = velocityProjections.getNormal();
        var otherVelocityNormalProjection = otherVelocityProjections.getNormal();

        velocityProjections.setNormal(getNewVelocityNormalProjection(
                velocityNormalProjection, otherVelocityNormalProjection,
                mass, otherMass,coefficient));
        collision.getPair().first().setVelocity(velocityProjections.recoverVelocity());

        otherVelocityProjections.setNormal(getNewVelocityNormalProjection(
                otherVelocityNormalProjection, velocityNormalProjection,
                otherMass, mass, coefficient));
        collision.getPair().second().setVelocity(otherVelocityProjections.recoverVelocity());
    }

    private static double getNewVelocityNormalProjection(
            double velocityNormalProjection, double otherVelocityNormalProjection,
            double mass, double otherMass, double coefficient
    ) {
        return coefficient * (- Math.abs(mass - otherMass) * velocityNormalProjection
                + 2 * otherMass * otherVelocityNormalProjection) / (mass + otherMass);
    }

    private static Position findClosestIntersectionPoint(Position position, Set<Position> intersectionPoints) {
        var iterator = intersectionPoints.iterator();
        var closest = iterator.next();
        var closestDistance = position.distanceTo(closest);
        while (iterator.hasNext()) {
            var point = iterator.next();
            var distance = position.distanceTo(point);
            if (distance < closestDistance) {
                closest = point;
                closestDistance = distance;
            }
        }
        return closest;
    }
}
