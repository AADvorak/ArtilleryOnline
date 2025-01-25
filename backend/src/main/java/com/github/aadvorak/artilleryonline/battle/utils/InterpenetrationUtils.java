package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.HalfCircle;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;

public class InterpenetrationUtils {

    public static double getWheelWheelInterpenetration(Circle wheel1, Circle wheel2) {
        return getCirclesInterpenetration(wheel1, wheel2);
    }

    public static double getWheelVehicleInterpenetration(Circle wheel, HalfCircle vehicle) {
        if (wheel.center().distanceTo(vehicle.center()) > wheel.radius() + vehicle.radius()) {
            return 0.0;
        }
        var circle = vehicle.circle();
        var intersectionPoints = GeometryUtils.getCirclesIntersectionPoints(wheel, circle);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle = GeometryUtils.getPointAngleInCircle(vehicle.center(), intersectionPoint);
            if (pointAngle > vehicle.angle() && pointAngle < vehicle.angle() + Math.PI) {
                return getCirclesInterpenetration(wheel, circle);
            }
        }
        var bottom = vehicle.chord();
        return getSegmentAndCircleInterpenetration(bottom, wheel);
    }

    public static double getVehicleVehicleInterpenetration(HalfCircle vehicle1, HalfCircle vehicle2) {
        var circle1 = vehicle1.circle();
        var circle2 = vehicle2.circle();
        var intersectionPoints = GeometryUtils.getCirclesIntersectionPoints(circle1, circle2);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle1 = GeometryUtils.getPointAngleInCircle(vehicle1.center(), intersectionPoint);
            var pointAngle2 = GeometryUtils.getPointAngleInCircle(vehicle2.center(), intersectionPoint);
            if (pointAngle1 > vehicle1.angle() && pointAngle2 > vehicle2.angle()
                    && pointAngle1 < vehicle1.angle() + Math.PI && pointAngle2 < vehicle2.angle() + Math.PI) {
                return getCirclesInterpenetration(circle1, circle2);
            }
        }
        var bottom1 = vehicle1.chord();
        if (isVehicleBottomAndOtherVehicleTopCollide(bottom1, vehicle2)) {
            return getSegmentAndCircleInterpenetration(bottom1, circle2);
        }
        var bottom2 = vehicle2.chord();
        if (isVehicleBottomAndOtherVehicleTopCollide(bottom2, vehicle1)) {
            return getSegmentAndCircleInterpenetration(bottom2, circle1);
        }
        return 0.0;
    }

    private static boolean isVehicleBottomAndOtherVehicleTopCollide(Segment bottom, HalfCircle otherVehicle) {
        var intersectionPoints = GeometryUtils.getSegmentAndCircleIntersectionPoints(bottom, otherVehicle.circle());
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle = GeometryUtils.getPointAngleInCircle(otherVehicle.center(), intersectionPoint);
            if (pointAngle > otherVehicle.angle() && pointAngle < otherVehicle.angle() + Math.PI) {
                return true;
            }
        }
        return false;
    }

    private static double getCirclesInterpenetration(Circle circle1, Circle circle2) {
        var distance = circle1.center().distanceTo(circle2.center());
        var minDistance = circle1.radius() + circle2.radius();
        return distance < minDistance ? minDistance - distance : 0.0;
    }

    private static double getSegmentAndCircleInterpenetration(Segment segment, Circle circle) {
        var distance = GeometryUtils.getDistanceFromPointToSegment(circle.center(), segment);
        return distance < circle.radius() ? circle.radius() - distance : 0.0;
    }
}
