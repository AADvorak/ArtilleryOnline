package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Position;

public class InterpenetrationUtils {

    public static double getWheelWheelInterpenetration(
            Position position1, Position position2,
            double radius1, double radius2
    ) {
        return GeometryUtils.getCirclesInterpenetration(position1, position2, radius1, radius2);
    }

    public static double getWheelVehicleInterpenetration(
            Position wheelPosition, Position vehiclePosition,
            double vehicleAngle,
            double wheelRadius, double vehicleRadius
    ) {
        if (wheelPosition.distanceTo(vehiclePosition) > vehicleRadius + wheelRadius) {
            return 0.0;
        }
        var intersectionPoints = GeometryUtils.getCirclesIntersectionPoints(wheelPosition, vehiclePosition,
                wheelRadius, vehicleRadius);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle = GeometryUtils.getPointAngleInCircle(vehiclePosition, intersectionPoint);
            if (pointAngle > vehicleAngle && pointAngle < vehicleAngle + Math.PI) {
                return GeometryUtils.getCirclesInterpenetration(wheelPosition, vehiclePosition,
                        wheelRadius, vehicleRadius);
            }
        }
        var bottom = getVehicleBottom(vehiclePosition, vehicleAngle, vehicleRadius);
        if (!GeometryUtils.getSegmentAndCircleIntersectionPoints(bottom, wheelPosition, wheelRadius).isEmpty()) {
            return GeometryUtils.getSegmentAndCircleInterpenetration(bottom, wheelPosition, wheelRadius);
        }
        return 0.0;
    }

    public static double getVehicleVehicleInterpenetration(
            Position position1, Position position2,
            double angle1, double angle2,
            double radius1, double radius2
    ) {
        var intersectionPoints = GeometryUtils.getCirclesIntersectionPoints(position1, position2, radius1, radius2);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle1 = GeometryUtils.getPointAngleInCircle(position1, intersectionPoint);
            var pointAngle2 = GeometryUtils.getPointAngleInCircle(position2, intersectionPoint);
            if (pointAngle1 > angle1 && pointAngle2 > angle2
                    && pointAngle1 < angle1 + Math.PI && pointAngle2 < angle2 + Math.PI) {
                return GeometryUtils.getCirclesInterpenetration(position1, position2, radius1, radius2);
            }
        }
        var bottom1 = getVehicleBottom(position1, angle1, radius1);
        if (isVehicleBottomAndOtherVehicleTopCollide(bottom1, position2, angle2, radius2)) {
            return GeometryUtils.getSegmentAndCircleInterpenetration(bottom1, position2, radius2);
        }
        var bottom2 = getVehicleBottom(position2, angle2, radius2);
        if (isVehicleBottomAndOtherVehicleTopCollide(bottom2, position1, angle1, radius1)) {
            return GeometryUtils.getSegmentAndCircleInterpenetration(bottom2, position1, radius1);
        }
        return 0.0;
    }

    private static Segment getVehicleBottom(Position position, double angle, double radius) {
        return new Segment(
                new Position()
                        .setX(position.getX() + radius * Math.cos(angle))
                        .setY(position.getY() + radius * Math.sin(angle)),
                new Position()
                        .setX(position.getX() - radius * Math.cos(angle))
                        .setY(position.getY() - radius * Math.sin(angle))
        );
    }

    private static boolean isVehicleBottomAndOtherVehicleTopCollide(
            Segment bottom, Position otherPosition,
            double otherAngle, double otherRadius
    ) {
        var intersectionPoints = GeometryUtils.getSegmentAndCircleIntersectionPoints(bottom, otherPosition, otherRadius);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle = GeometryUtils.getPointAngleInCircle(otherPosition, intersectionPoint);
            if (pointAngle > otherAngle && pointAngle < otherAngle + Math.PI) {
                return true;
            }
        }
        return false;
    }
}
