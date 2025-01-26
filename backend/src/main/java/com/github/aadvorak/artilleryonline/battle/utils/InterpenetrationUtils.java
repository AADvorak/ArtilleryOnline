package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Interpenetration;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.HalfCircle;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;

import java.util.Optional;

public class InterpenetrationUtils {

    public static Interpenetration getCirclesInterpenetration(Circle circle, Circle otherCircle) {
        var distance = circle.center().distanceTo(otherCircle.center());
        var minDistance = circle.radius() + otherCircle.radius();
        var depth = distance < minDistance ? minDistance - distance : 0.0;
        return Interpenetration.of(depth, circle.center(), otherCircle.center());
    }

    public static Interpenetration getCircleHalfCircleInterpenetration(Circle circle, HalfCircle otherHalfCircle) {
        if (circle.center().distanceTo(otherHalfCircle.center()) > circle.radius() + otherHalfCircle.radius()) {
            return null;
        }
        var otherCircle = otherHalfCircle.circle();
        var intersectionPoints = GeometryUtils.getCirclesIntersectionPoints(circle, otherCircle);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle = GeometryUtils.getPointAngleInCircle(otherHalfCircle.center(), intersectionPoint);
            if (pointAngle > otherHalfCircle.angle() && pointAngle < otherHalfCircle.angle() + Math.PI) {
                return getCirclesInterpenetration(circle, otherCircle);
            }
        }
        var otherBottom = otherHalfCircle.chord();
        return Optional.ofNullable(getSurfaceAndCircleInterpenetration(otherBottom, circle))
                .map(Interpenetration::inverted)
                .orElse(null);
    }

    public static Interpenetration getHalfCirclesInterpenetration(HalfCircle halfCircle, HalfCircle otherHalfCircle) {
        if (halfCircle.center().distanceTo(otherHalfCircle.center()) > halfCircle.radius() + otherHalfCircle.radius()) {
            return null;
        }
        var circle = halfCircle.circle();
        var otherCircle = otherHalfCircle.circle();
        var intersectionPoints = GeometryUtils.getCirclesIntersectionPoints(circle, otherCircle);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle1 = GeometryUtils.getPointAngleInCircle(halfCircle.center(), intersectionPoint);
            var pointAngle2 = GeometryUtils.getPointAngleInCircle(otherHalfCircle.center(), intersectionPoint);
            if (pointAngle1 > halfCircle.angle() && pointAngle2 > otherHalfCircle.angle()
                    && pointAngle1 < halfCircle.angle() + Math.PI && pointAngle2 < otherHalfCircle.angle() + Math.PI) {
                return getCirclesInterpenetration(circle, otherCircle);
            }
        }
        var bottom = halfCircle.chord();
        if (isHalfCircleBottomAndOtherHalfCircleTopInterpenetrate(bottom, otherHalfCircle)) {
            return getSurfaceAndCircleInterpenetration(bottom, otherCircle);
        }
        var otherBottom = otherHalfCircle.chord();
        if (isHalfCircleBottomAndOtherHalfCircleTopInterpenetrate(otherBottom, halfCircle)) {
            return Optional.ofNullable(getSurfaceAndCircleInterpenetration(otherBottom, circle))
                    .map(Interpenetration::inverted)
                    .orElse(null);
        }
        return null;
    }

    private static boolean isHalfCircleBottomAndOtherHalfCircleTopInterpenetrate(Segment bottom, HalfCircle otherHalfCircle) {
        var intersectionPoints = GeometryUtils.getSegmentAndCircleIntersectionPoints(bottom, otherHalfCircle.circle());
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle = GeometryUtils.getPointAngleInCircle(otherHalfCircle.center(), intersectionPoint);
            if (pointAngle > otherHalfCircle.angle() && pointAngle < otherHalfCircle.angle() + Math.PI) {
                return true;
            }
        }
        return false;
    }

    private static Interpenetration getSurfaceAndCircleInterpenetration(Segment surface, Circle circle) {
        var projection = GeometryUtils.getPointToSegmentProjection(circle.center(), surface);
        if (projection == null) {
            return null;
        }
        var distance = circle.center().distanceTo(projection);
        var depth = distance < circle.radius() ? circle.radius() - distance : 0.0;
        return Interpenetration.of(depth, projection, circle.center());
    }
}
