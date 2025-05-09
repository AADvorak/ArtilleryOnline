package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.HalfCircle;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;

import java.util.Set;
import java.util.stream.Collectors;

public class CollisionUtils {

    public static boolean collisionNotDetected(Calculations<?> object, Calculations<?> otherObject) {
        return otherObject.getCollisions().stream()
                .noneMatch(c -> object.getId().equals(c.getSecondId()));
    }

    public static Collision detectWithMissile(Calculations<?> calculations, Position position, Position nextPosition,
                                              MissileCalculations missile) {
        var missileSegment = new Segment(missile.getPositions().getHead(), missile.getPositions().getTail());
        var projectileTrace = new Segment(position, nextPosition);
        var intersectionPoint = GeometryUtils.getSegmentsIntersectionPoint(projectileTrace, missileSegment);
        if (intersectionPoint != null) {
            var projection = GeometryUtils.getPointToLineProjection(position, missileSegment);
            var contact = Contact.withUncheckedDepthOf(0.0,
                    position.vectorTo(projection).normalized(), intersectionPoint);
            return Collision.withMissile(calculations, missile, contact);
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
                    var pointAngle = vehiclePosition.angleTo(intersectionPoint);
                    return pointAngle > vehicleAngle && pointAngle < vehicleAngle + Math.PI;
                }).collect(Collectors.toSet());
        if (!intersectionPoints.isEmpty()) {
            var intersectionPoint = findClosestIntersectionPoint(position, intersectionPoints);
            var contact = Contact.withUncheckedDepthOf(0.0,
                    intersectionPoint.vectorTo(vehiclePosition).normalized(), vehiclePosition);
            return Collision.withVehicle(calculations, vehicle, contact);
        }
        var vehicleBottom = vehicleShape.chord();
        var intersectionPoint = GeometryUtils.getSegmentsIntersectionPoint(projectileTrace, vehicleBottom);
        if (intersectionPoint != null) {
            var projection = GeometryUtils.getPointToLineProjection(position, vehicleBottom);
            var contact = Contact.withUncheckedDepthOf(0.0,
                    position.vectorTo(projection).normalized(), intersectionPoint);
            return Collision.withVehicle(calculations, vehicle, contact);
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
            var contact = Contact.withUncheckedDepthOf(0.0,
                    intersectionPoint.vectorTo(wheelPosition).normalized(), intersectionPoint);
            return Collision.withVehicle(calculations, wheel, contact);
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
            var contact = Contact.withUncheckedDepthOf(0.0,
                    intersectionPoint.vectorTo(dronePosition).normalized(), intersectionPoint);
            return Collision.withDrone(calculations, drone, contact);
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
            var projection = GeometryUtils.getPointToLineProjection(position, rightEngineShape);
            var contact = Contact.withUncheckedDepthOf(0.0,
                    position.vectorTo(projection).normalized(), rightEngineIntersectionPoint);
            return Collision.withDrone(calculations, drone, contact);
        }

        var leftEngineCenterPosition = dronePosition.shifted(enginesRadius, droneAngle - Math.PI)
                .shifted(droneRadius, droneAngle + Math.PI / 2);
        var leftEngineShape = new Segment(
                leftEngineCenterPosition.shifted(enginesRadius / 3, droneAngle - Math.PI),
                leftEngineCenterPosition.shifted(enginesRadius / 3, droneAngle)
        );
        var leftEngineIntersectionPoint = GeometryUtils.getSegmentsIntersectionPoint(projectileTrace, leftEngineShape);
        if (leftEngineIntersectionPoint != null) {
            var projection = GeometryUtils.getPointToLineProjection(position, leftEngineShape);
            var contact = Contact.withUncheckedDepthOf(0.0,
                    position.vectorTo(projection).normalized(), leftEngineIntersectionPoint);
            return Collision.withDrone(calculations, drone, contact);
        }
        return null;
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
