package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Position;

import java.util.Set;
import java.util.stream.Collectors;

public class CollideUtils {

    public static boolean isWheelVehicleCollide(
            Position wheelPosition, Position vehiclePosition,
            double vehicleAngle,
            double wheelRadius, double vehicleRadius
    ) {
        if (wheelPosition.distanceTo(vehiclePosition) > vehicleRadius + wheelRadius) {
            return false;
        }
        var intersectionPoints = getCirclesIntersectionPoints(wheelPosition, vehiclePosition, wheelRadius, vehicleRadius);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle = getPointAngleInCircle(vehiclePosition, intersectionPoint);
            if (pointAngle > vehicleAngle && pointAngle < vehicleAngle + Math.PI) {
                return true;
            }
        }
        var bottom = getVehicleBottom(vehiclePosition, vehicleAngle, vehicleRadius);
        return !getSegmentAndCircleIntersectionPoints(bottom, wheelPosition, wheelRadius).isEmpty();
    }

    public static boolean isVehicleVehicleCollide(
            Position position1, Position position2,
            double angle1, double angle2,
            double radius1, double radius2
    ) {
        var intersectionPoints = getCirclesIntersectionPoints(position1, position2, radius1, radius2);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle1 = getPointAngleInCircle(position1, intersectionPoint);
            var pointAngle2 = getPointAngleInCircle(position2, intersectionPoint);
            if (pointAngle1 > angle1 && pointAngle2 > angle2
                    && pointAngle1 < angle1 + Math.PI && pointAngle2 < angle2 + Math.PI) {
                return true;
            }
        }
        return isVehicleBottomAndOtherVehicleTopCollide(position1, position2, angle1, angle2, radius1, radius2)
                || isVehicleBottomAndOtherVehicleTopCollide(position2, position1, angle2, angle1, radius2, radius1);
    }

    public static Segment getVehicleBottom(Position position, double angle, double radius) {
        return new Segment(
                new Position()
                        .setX(position.getX() + radius * Math.cos(angle))
                        .setY(position.getY() + radius * Math.sin(angle)),
                new Position()
                        .setX(position.getX() - radius * Math.cos(angle))
                        .setY(position.getY() - radius * Math.sin(angle))
        );
    }

    public static boolean isVehicleBottomAndOtherVehicleTopCollide(
            Position position, Position otherPosition,
            double angle, double otherAngle,
            double radius, double otherRadius
    ) {
        var bottom = getVehicleBottom(position, angle, radius);
        var intersectionPoints = getSegmentAndCircleIntersectionPoints(bottom, otherPosition, otherRadius);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle = getPointAngleInCircle(otherPosition, intersectionPoint);
            if (pointAngle > otherAngle && pointAngle < otherAngle + Math.PI) {
                return true;
            }
        }
        return false;
    }

    public static double getPointAngleInCircle(Position center, Position point) {
        var angle = Math.atan((point.getY() - center.getY()) / (point.getX() - center.getX()));
        if (point.getX() > center.getX()) {
            return angle;
        }
        return point.getY() < center.getY() ? angle - Math.PI : angle + Math.PI;
    }

    public static Set<Position> getSegmentAndCircleIntersectionPoints(
            Segment segment, Position circleCenter, double circleRadius
    ) {
        if (Math.abs(segment.begin().getX() - segment.end().getX())
                > Math.abs(segment.begin().getY() - segment.end().getY())) {
            return getSegmentAndCircleIntersectionPointsByX(segment, circleCenter, circleRadius);
        }
        return getSegmentAndCircleIntersectionPointsByY(segment, circleCenter, circleRadius);
    }

    public static Set<Position> getSegmentAndCircleIntersectionPointsByX(
            Segment segment, Position circleCenter, double circleRadius
    ) {
        var lineA = (segment.begin().getY() - segment.end().getY())
                / (segment.begin().getX() - segment.end().getX());
        var lineB = segment.end().getY() - lineA * segment.end().getX();
        var circleA = 1 + Math.pow(lineA, 2);
        var circleB = 2 * lineA * (lineB - circleCenter.getY()) - 2 * circleCenter.getX();
        var circleC = Math.pow(circleCenter.getX(), 2) - Math.pow(circleRadius, 2)
                + Math.pow(lineB - circleCenter.getY(), 2);
        var discriminant = Math.pow(circleB, 2) - 4 * circleA * circleC;
        if (discriminant < 0) {
            return Set.of();
        }
        var xMin = Math.min(segment.begin().getX(), segment.end().getX());
        var xMax = Math.max(segment.begin().getX(), segment.end().getX());
        var xSet = discriminant == 0
                ? Set.of(-circleB / (2 * circleA))
                : Set.of((-circleB + Math.sqrt(discriminant)) / (2 * circleA),
                (-circleB - Math.sqrt(discriminant)) / (2 * circleA));
        return xSet.stream()
                .filter(x -> x <= xMax && x >= xMin)
                .map(x -> new Position().setX(x).setY(lineA * x + lineB))
                .collect(Collectors.toSet());
    }

    public static Set<Position> getSegmentAndCircleIntersectionPointsByY(
            Segment segment, Position circleCenter, double circleRadius
    ) {
        var lineA = (segment.begin().getX() - segment.end().getX())
                / (segment.begin().getY() - segment.end().getY());
        var lineB = segment.end().getX() - lineA * segment.end().getY();
        var circleA = 1 + Math.pow(lineA, 2);
        var circleB = 2 * lineA * (lineB - circleCenter.getX()) - 2 * circleCenter.getY();
        var circleC = Math.pow(circleCenter.getY(), 2) - Math.pow(circleRadius, 2)
                + Math.pow(lineB - circleCenter.getX(), 2);
        var discriminant = Math.pow(circleB, 2) - 4 * circleA * circleC;
        if (discriminant < 0) {
            return Set.of();
        }
        var yMin = Math.min(segment.begin().getY(), segment.end().getY());
        var yMax = Math.max(segment.begin().getY(), segment.end().getY());
        var ySet = discriminant == 0
                ? Set.of(-circleB / (2 * circleA))
                : Set.of((-circleB + Math.sqrt(discriminant)) / (2 * circleA),
                (-circleB - Math.sqrt(discriminant)) / (2 * circleA));
        return ySet.stream()
                .filter(y -> y <= yMax && y >= yMin)
                .map(y -> new Position().setX(lineA * y + lineB).setY(y))
                .collect(Collectors.toSet());
    }

    // https://debug64.page/ru/posts/math/intersection_of_two_circles/
    public static Set<Position> getCirclesIntersectionPoints(
            Position center1, Position center2, double radius1, double radius2
    ) {
        var diffX = center2.getX() - center1.getX();
        var diffY = center2.getY() - center1.getY();
        var c = -0.5 * (Math.pow(radius2, 2) - Math.pow(radius1, 2) - Math.pow(diffX, 2) - Math.pow(diffY, 2));
        var a = Math.pow(diffX, 2) + Math.pow(diffY, 2);
        if (diffX > diffY) {
            var b = -2.0 * diffY * c;
            var e = Math.pow(c, 2) - Math.pow(radius1, 2) * Math.pow(diffX, 2);
            var d = Math.pow(b, 2) - 4 * a * e;
            if (d < 0) {
                return Set.of();
            }
            if (d == 0) {
                var y = -b / (2 * a);
                var x = (c - y * diffY) / diffX;
                return Set.of(new Position().setX(x + center1.getX()).setY(y + center1.getY()));
            }
            var y1 = -b + Math.sqrt(d) / (2 * a);
            var y2 = -b - Math.sqrt(d) / (2 * a);
            var x1 = (c - y1 * diffY) / diffX;
            var x2 = (c - y2 * diffY) / diffX;
            return Set.of(new Position().setX(x1 + center1.getX()).setY(y1 + center1.getY()),
                    new Position().setX(x2 + center1.getX()).setY(y2 + center1.getY()));
        } else {
            var b = -2.0 * diffX * c;
            var e = Math.pow(c, 2) - Math.pow(radius1, 2) * Math.pow(diffY, 2);
            var d = Math.pow(b, 2) - 4 * a * e;
            if (d < 0) {
                return Set.of();
            }
            if (d == 0) {
                var x = -b / (2 * a);
                var y = (c - x * diffX) / diffY;
                return Set.of(new Position().setX(x + center1.getX()).setY(y + center1.getY()));
            }
            var x1 = -b + Math.sqrt(d) / (2 * a);
            var x2 = -b - Math.sqrt(d) / (2 * a);
            var y1 = (c - x1 * diffX) / diffY;
            var y2 = (c - x2 * diffX) / diffY;
            return Set.of(new Position().setX(x1 + center1.getX()).setY(y1 + center1.getY()),
                    new Position().setX(x2 + center1.getX()).setY(y2 + center1.getY()));
        }
    }

    public record Segment(Position begin, Position end) {
    }
}
