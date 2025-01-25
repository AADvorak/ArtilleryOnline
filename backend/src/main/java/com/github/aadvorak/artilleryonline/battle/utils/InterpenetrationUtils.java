package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.HalfCircle;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;

public class InterpenetrationUtils {

    public static double getCirclesInterpenetration(Circle circle1, Circle circle2) {
        var distance = circle1.center().distanceTo(circle2.center());
        var minDistance = circle1.radius() + circle2.radius();
        return distance < minDistance ? minDistance - distance : 0.0;
    }

    public static double getCircleHalfCircleInterpenetration(Circle circle, HalfCircle halfCircle) {
        if (circle.center().distanceTo(halfCircle.center()) > circle.radius() + halfCircle.radius()) {
            return 0.0;
        }
        var otherCircle = halfCircle.circle();
        var intersectionPoints = GeometryUtils.getCirclesIntersectionPoints(circle, otherCircle);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle = GeometryUtils.getPointAngleInCircle(halfCircle.center(), intersectionPoint);
            if (pointAngle > halfCircle.angle() && pointAngle < halfCircle.angle() + Math.PI) {
                return getCirclesInterpenetration(circle, otherCircle);
            }
        }
        var bottom = halfCircle.chord();
        return getSegmentAndCircleInterpenetration(bottom, circle);
    }

    public static double getHalfCirclesInterpenetration(HalfCircle halfCircle1, HalfCircle halfCircle2) {
        var circle1 = halfCircle1.circle();
        var circle2 = halfCircle2.circle();
        var intersectionPoints = GeometryUtils.getCirclesIntersectionPoints(circle1, circle2);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle1 = GeometryUtils.getPointAngleInCircle(halfCircle1.center(), intersectionPoint);
            var pointAngle2 = GeometryUtils.getPointAngleInCircle(halfCircle2.center(), intersectionPoint);
            if (pointAngle1 > halfCircle1.angle() && pointAngle2 > halfCircle2.angle()
                    && pointAngle1 < halfCircle1.angle() + Math.PI && pointAngle2 < halfCircle2.angle() + Math.PI) {
                return getCirclesInterpenetration(circle1, circle2);
            }
        }
        var bottom1 = halfCircle1.chord();
        if (isHalfCircleBottomAndOtherHalfCircleTopInterpenetrate(bottom1, halfCircle2)) {
            return getSegmentAndCircleInterpenetration(bottom1, circle2);
        }
        var bottom2 = halfCircle2.chord();
        if (isHalfCircleBottomAndOtherHalfCircleTopInterpenetrate(bottom2, halfCircle1)) {
            return getSegmentAndCircleInterpenetration(bottom2, circle1);
        }
        return 0.0;
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

    private static double getSegmentAndCircleInterpenetration(Segment segment, Circle circle) {
        var distance = GeometryUtils.getDistanceFromPointToSegment(circle.center(), segment);
        return distance < circle.radius() ? circle.radius() - distance : 0.0;
    }
}
