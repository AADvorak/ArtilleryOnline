package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.common.lines.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollisionUtils {

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
        var projectileTrace = new Segment(position, nextPosition);
        var vehiclePart = BodyPart.of(BodyPosition.of(vehicle.getGeometryPosition(),
                vehicle.getModel().getState().getPosition().getAngle()),
                vehicle.getModel().getSpecs().getTurretShape());
        var intersectionPointsMap = new HashMap<Position, ContactAndHitSurface>();
        if (vehiclePart instanceof HalfCircle halfCircle) {
            GeometryUtils.getSegmentAndHalfCircleIntersectionPoints(projectileTrace, halfCircle)
                    .forEach(intersectionPoint -> {
                        var contact = Contact.withUncheckedDepthOf(0.0,
                                intersectionPoint.vectorTo(halfCircle.center()).normalized(), intersectionPoint);
                        intersectionPointsMap.put(intersectionPoint, new ContactAndHitSurface(contact, HitSurface.TOP));
                    });
            findIntersectionPointAndPutToMap(projectileTrace, halfCircle.chord(), HitSurface.BOTTOM,  intersectionPointsMap);
        }
        if (vehiclePart instanceof Trapeze trapeze) {
            List.of(trapeze.left(), trapeze.right()).forEach(side ->
                    findIntersectionPointAndPutToMap(projectileTrace, side, HitSurface.SIDE, intersectionPointsMap));
            findIntersectionPointAndPutToMap(projectileTrace, trapeze.bottom(), HitSurface.BOTTOM, intersectionPointsMap);
            findIntersectionPointAndPutToMap(projectileTrace, trapeze.top(), HitSurface.TOP, intersectionPointsMap);
        }
        if (!intersectionPointsMap.isEmpty()) {
            var closest = GeometryUtils.findClosestPosition(position, intersectionPointsMap.keySet());
            var contactAndHitSurface = intersectionPointsMap.get(closest);
            return Collision.withVehicle(calculations, vehicle, contactAndHitSurface.contact(),
                    contactAndHitSurface.hitSurface());
        }
        return null;
    }

    public static Collision detectWithBox(Calculations<?> calculations, Position position, Position nextPosition,
                                          BoxCalculations box) {
        var projectileTrace = new Segment(position, nextPosition);
        var bodyPart = BodyPart.of(box.getGeometryNextPosition(), box.getModel().getSpecs().getShape());
        if (bodyPart instanceof Trapeze trapeze) {
            var contact = findContact(trapeze, projectileTrace);
            if (contact != null) {
                return Collision.withBox(calculations, box, contact);
            }
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
            var intersectionPoint = GeometryUtils.findClosestPosition(position, intersectionPoints);
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
            var intersectionPoint = GeometryUtils.findClosestPosition(position, intersectionPoints);
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

    private static Contact findContact(Trapeze trapeze, Segment projectileTrace) {
        var polygon = new Polygon(trapeze);
        for (var side : polygon.sides()) {
            var intersectionPoint = GeometryUtils.getSegmentsIntersectionPoint(projectileTrace, side);
            if (intersectionPoint != null) {
                return Contact.withUncheckedDepthOf(0.0,
                        side.normal(), intersectionPoint);
            }
        }
        return null;
    }

    private static void findIntersectionPointAndPutToMap(Segment projectileTrace, Segment side, HitSurface hitSurface,
                                                  Map<Position, ContactAndHitSurface> intersectionPointsMap) {
        var intersectionPoint = GeometryUtils.getSegmentsIntersectionPoint(projectileTrace, side);
        if (intersectionPoint != null) {
            var contact = Contact.withUncheckedDepthOf(0.0,
                    side.normal(), intersectionPoint);
            intersectionPointsMap.put(intersectionPoint, new ContactAndHitSurface(contact, hitSurface));
        }
    }
}
