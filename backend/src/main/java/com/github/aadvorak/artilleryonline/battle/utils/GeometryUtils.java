package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.HalfCircle;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;

import java.util.Set;
import java.util.stream.Collectors;

public class GeometryUtils {

    public static Position findClosestPosition(Position position, Set<Position> positions) {
        var iterator = positions.iterator();
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

    public static boolean isPointLyingOnArc(double pointAngle, double beginAngle, double endAngle) {
        var shiftedPointAngle = pointAngle + 2 * Math.PI;
        return pointAngle > beginAngle && pointAngle < endAngle
                || shiftedPointAngle > beginAngle && shiftedPointAngle < endAngle;
    }

    public static double calculateAngleDiff(double objectAngle, double targetAngle) {
        double diff = targetAngle - objectAngle;
        if (Math.abs(diff) > Math.PI) {
            if (diff > 0) {
                return 2 * Math.PI - diff;
            } else {
                return 2 * Math.PI + diff;
            }
        } else {
            return diff;
        }
    }

    public static Position getPointToLineProjection(Position point, Segment line) {
        return getPointToSegmentProjection(point, line, true);
    }

    public static Position getPointToSegmentProjection(Position point, Segment segment) {
        return getPointToSegmentProjection(point, segment, false);
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

    public static Set<Position> getSegmentAndHalfCircleIntersectionPoints(Segment segment, HalfCircle halfCircle) {
        return getSegmentAndCircleIntersectionPoints(segment, halfCircle.circle()).stream()
                .filter(point -> {
                    var pointAngle = halfCircle.center().angleTo(point);
                    return isPointLyingOnArc(pointAngle, halfCircle.angle(), halfCircle.angle() + Math.PI);
                })
                .collect(Collectors.toSet());
    }

    public static Set<Position> getSegmentAndCircleIntersectionPoints(Segment segment, Circle circle) {
        if (Math.abs(segment.begin().getX() - segment.end().getX())
                > Math.abs(segment.begin().getY() - segment.end().getY())) {
            return getSegmentAndCircleIntersectionPointsByX(segment, circle);
        }
        return getSegmentAndCircleIntersectionPointsByY(segment, circle);
    }

    public static Set<Position> getCirclesIntersectionPoints(Circle circle1, Circle circle2) {
        var center1 = circle1.center();
        var center2 = circle2.center();
        var radius1 = circle1.radius();
        var radius2 = circle2.radius();
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

    private static Set<Position> getSegmentAndCircleIntersectionPointsByX(Segment segment, Circle circle) {
        var lineA = (segment.begin().getY() - segment.end().getY())
                / (segment.begin().getX() - segment.end().getX());
        var lineB = segment.end().getY() - lineA * segment.end().getX();
        var circleA = 1 + Math.pow(lineA, 2);
        var circleB = 2 * lineA * (lineB - circle.center().getY()) - 2 * circle.center().getX();
        var circleC = Math.pow(circle.center().getX(), 2) - Math.pow(circle.radius(), 2)
                + Math.pow(lineB - circle.center().getY(), 2);
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

    private static Set<Position> getSegmentAndCircleIntersectionPointsByY(Segment segment, Circle circle) {
        var lineA = (segment.begin().getX() - segment.end().getX())
                / (segment.begin().getY() - segment.end().getY());
        var lineB = segment.end().getX() - lineA * segment.end().getY();
        var circleA = 1 + Math.pow(lineA, 2);
        var circleB = 2 * lineA * (lineB - circle.center().getX()) - 2 * circle.center().getY();
        var circleC = Math.pow(circle.center().getY(), 2) - Math.pow(circle.radius(), 2)
                + Math.pow(lineB - circle.center().getX(), 2);
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

    private static Position getPointToSegmentProjection(Position point, Segment segment, boolean notCheckInside) {
        var A = point.getX() - segment.begin().getX();
        var B = point.getY() - segment.begin().getY();
        var C = segment.end().getX() - segment.begin().getX();
        var D = segment.end().getY() - segment.begin().getY();

        var dot = A * C + B * D;
        var squareLength = C * C + D * D;
        var param = squareLength != 0 ? dot / squareLength : -1.0;

        var projection = new Position()
                .setX(segment.begin().getX() + param * C)
                .setY(segment.begin().getY() + param * D);
        if (notCheckInside) {
            return projection;
        }
        if (param >= 0 && param <= 1) {
            return projection;
        }
        return null;
    }
}
