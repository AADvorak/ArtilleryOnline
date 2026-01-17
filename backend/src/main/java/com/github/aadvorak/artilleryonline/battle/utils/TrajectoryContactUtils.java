package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.common.lines.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrajectoryContactUtils {

    public static ContactAndHitSurface detectWithVehicle(Segment trajectory, VehicleCalculations vehicle) {
        var vehiclePart = BodyPart.of(BodyPosition.of(vehicle.getGeometryPosition(),
                        vehicle.getModel().getState().getPosition().getAngle()),
                vehicle.getModel().getSpecs().getTurretShape());
        var intersectionPointsMap = new HashMap<Position, ContactAndHitSurface>();
        if (vehiclePart instanceof HalfCircle halfCircle) {
            GeometryUtils.getSegmentAndHalfCircleIntersectionPoints(trajectory, halfCircle)
                    .forEach(intersectionPoint -> {
                        var contact = Contact.withUncheckedDepthOf(0.0,
                                intersectionPoint.vectorTo(halfCircle.center()).normalized(), intersectionPoint);
                        intersectionPointsMap.put(intersectionPoint, new ContactAndHitSurface(contact, HitSurface.TOP));
                    });
            findIntersectionPointAndPutToMap(trajectory, halfCircle.chord(), HitSurface.BOTTOM,  intersectionPointsMap);
        }
        if (vehiclePart instanceof Trapeze trapeze) {
            List.of(trapeze.left(), trapeze.right()).forEach(side ->
                    findIntersectionPointAndPutToMap(trajectory, side, HitSurface.SIDE, intersectionPointsMap));
            findIntersectionPointAndPutToMap(trajectory, trapeze.bottom(), HitSurface.BOTTOM, intersectionPointsMap);
            findIntersectionPointAndPutToMap(trajectory, trapeze.top(), HitSurface.TOP, intersectionPointsMap);
        }
        if (!intersectionPointsMap.isEmpty()) {
            var closest = GeometryUtils.findClosestPosition(trajectory.begin(), intersectionPointsMap.keySet());
            return intersectionPointsMap.get(closest);
        }
        return null;
    }

    public static Contact detectWithWheel(Segment trajectory, WheelCalculations wheel) {
        var wheelPosition = wheel.getPosition();
        var wheelRadius = wheel.getModel().getSpecs().getWheelRadius();
        var wheelShape = new Circle(wheelPosition, wheelRadius);
        var intersectionPoints = GeometryUtils.getSegmentAndCircleIntersectionPoints(trajectory, wheelShape);
        if (!intersectionPoints.isEmpty()) {
            var intersectionPoint = GeometryUtils.findClosestPosition(trajectory.begin(), intersectionPoints);
            return Contact.withUncheckedDepthOf(0.0,
                    intersectionPoint.vectorTo(wheelPosition).normalized(), intersectionPoint);
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
