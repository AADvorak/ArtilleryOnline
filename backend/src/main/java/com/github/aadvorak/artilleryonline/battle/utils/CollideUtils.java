package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Position;

public class CollideUtils {

    public static boolean isWheelVehicleCollide(
            Position wheelPosition, Position vehiclePosition,
            double vehicleAngle,
            double wheelRadius, double vehicleRadius
    ) {
        if (wheelPosition.distanceTo(vehiclePosition) > vehicleRadius + wheelRadius) {
            return false;
        }
        var intersectionPoints = GeometryUtils.getCirclesIntersectionPoints(wheelPosition, vehiclePosition,
                wheelRadius, vehicleRadius);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle = GeometryUtils.getPointAngleInCircle(vehiclePosition, intersectionPoint);
            if (pointAngle > vehicleAngle && pointAngle < vehicleAngle + Math.PI) {
                return true;
            }
        }
        var bottom = getVehicleBottom(vehiclePosition, vehicleAngle, vehicleRadius);
        return !GeometryUtils.getSegmentAndCircleIntersectionPoints(bottom, wheelPosition, wheelRadius).isEmpty();
    }

    public static boolean isVehicleVehicleCollide(
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
                return true;
            }
        }
        return isVehicleBottomAndOtherVehicleTopCollide(position1, position2, angle1, angle2, radius1, radius2)
                || isVehicleBottomAndOtherVehicleTopCollide(position2, position1, angle2, angle1, radius2, radius1);
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
            Position position, Position otherPosition,
            double angle, double otherAngle,
            double radius, double otherRadius
    ) {
        var bottom = getVehicleBottom(position, angle, radius);
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
