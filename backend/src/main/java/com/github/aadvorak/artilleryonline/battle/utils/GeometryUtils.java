package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Position;

import java.util.Set;
import java.util.stream.Collectors;

public class GeometryUtils {

    public static double getCirclesInterpenetration(
            Position center1, Position center2,
            double radius1, double radius2
    ) {
        var distance = center1.distanceTo(center2);
        var minDistance = radius1 + radius2;
        return distance < minDistance ? minDistance - distance : 0.0;
    }

    public static double getSegmentAndCircleInterpenetration(Segment segment, Position center, double radius) {
        var distance = getDistanceFromPointToSegment(center, segment);
        return distance < radius ? radius - distance : 0.0;
    }

    public static double getDistanceFromPointToSegment(Position point, Segment segment) {
        var A = point.getX() - segment.begin().getX();
        var B = point.getY() - segment.begin().getY();
        var C = segment.end().getX() - segment.begin().getX();
        var D = segment.end().getY() - segment.begin().getY();

        var dot = A * C + B * D;
        var squareLength = C * C + D * D;
        var param =  squareLength != 0 ? dot / squareLength : -1.0;

        var xx = 0.0;
        var yy = 0.0;

        if (param < 0) {
            xx = segment.begin().getX();
            yy = segment.begin().getY();
        } else if (param > 1) {
            xx = segment.end().getX();
            yy = segment.end().getY();
        } else {
            xx = segment.begin().getX() + param * C;
            yy = segment.begin().getY() + param * D;
        }

        var dx = point.getX() - xx;
        var dy = point.getY() - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double getPointAngleInCircle(Position center, Position point) {
        var angle = Math.atan((point.getY() - center.getY()) / (point.getX() - center.getX()));
        if (point.getX() > center.getX()) {
            return angle;
        }
        return point.getY() < center.getY() ? angle - Math.PI : angle + Math.PI;
    }

    public static boolean isSegmentCrossingCircle(
            Segment segment, Position circleCenter, double circleRadius
    ) {
        return !getSegmentAndCircleIntersectionPoints(segment, circleCenter, circleRadius).isEmpty();
    }

    public static Position getSegmentsIntersectionPoint(Segment s1, Segment s2) {
        var x1 = s1.begin().getX();
        var y1 = s1.begin().getY();
        var x2 = s1.end().getX();
        var y2 = s1.end().getY();

        var x3 = s2.begin().getX();
        var y3 = s2.begin().getY();
        var x4 = s2.end().getX();
        var y4 = s2.end().getY();

        var denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        if (Math.abs(denominator) < 1e-9) {
            return null;
        }

        var t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denominator;
        var u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / denominator;

        if (t > 0 && t < 1 && u > 0 && u < 1) {
            var x = x1 + t * (x2 - x1);
            var y = y1 + t * (y2 - y1);
            return new Position().setX(x).setY(y);
        } else {
            return null;
        }
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

    private static Set<Position> getSegmentAndCircleIntersectionPointsByX(
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

    private static Set<Position> getSegmentAndCircleIntersectionPointsByY(
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
}
