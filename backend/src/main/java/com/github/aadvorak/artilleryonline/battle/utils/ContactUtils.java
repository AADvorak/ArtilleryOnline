package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.HalfCircle;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;

import java.util.Optional;

public class ContactUtils {

    public static Contact getCirclesContact(Circle circle, Circle otherCircle) {
        var distance = circle.center().distanceTo(otherCircle.center());
        var minDistance = circle.radius() + otherCircle.radius();
        var depth = distance < minDistance ? minDistance - distance : 0.0;
        var normal = circle.center().vectorTo(otherCircle.center()).normalized();
        var position = circle.center().shifted(normal.multiply(circle.radius() - depth / 2));
        return Contact.of(depth, normal, position);
    }

    public static Contact getCircleHalfCircleContact(Circle circle, HalfCircle otherHalfCircle) {
        if (circle.center().distanceTo(otherHalfCircle.center()) > circle.radius() + otherHalfCircle.radius()) {
            return null;
        }
        var otherCircle = otherHalfCircle.circle();
        var intersectionPoints = GeometryUtils.getCirclesIntersectionPoints(circle, otherCircle);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle = otherHalfCircle.center().angleTo(intersectionPoint);
            if (pointAngle > otherHalfCircle.angle() && pointAngle < otherHalfCircle.angle() + Math.PI) {
                return getCirclesContact(circle, otherCircle);
            }
        }
        var otherBottom = otherHalfCircle.chord();
        return Optional.ofNullable(getSurfaceAndCircleContact(otherBottom, circle))
                .map(Contact::inverted)
                .orElse(null);
    }

    public static Contact getHalfCirclesContact(HalfCircle halfCircle, HalfCircle otherHalfCircle) {
        if (halfCircle.center().distanceTo(otherHalfCircle.center()) > halfCircle.radius() + otherHalfCircle.radius()) {
            return null;
        }
        var circle = halfCircle.circle();
        var otherCircle = otherHalfCircle.circle();
        var intersectionPoints = GeometryUtils.getCirclesIntersectionPoints(circle, otherCircle);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle1 = halfCircle.center().angleTo(intersectionPoint);
            var pointAngle2 = otherHalfCircle.center().angleTo(intersectionPoint);
            if (pointAngle1 > halfCircle.angle() && pointAngle2 > otherHalfCircle.angle()
                    && pointAngle1 < halfCircle.angle() + Math.PI && pointAngle2 < otherHalfCircle.angle() + Math.PI) {
                return getCirclesContact(circle, otherCircle);
            }
        }
        var bottom = halfCircle.chord();
        if (isHalfCircleBottomAndOtherHalfCircleTopContact(bottom, otherHalfCircle)) {
            return getSurfaceAndCircleContact(bottom, otherCircle);
        }
        var otherBottom = otherHalfCircle.chord();
        if (isHalfCircleBottomAndOtherHalfCircleTopContact(otherBottom, halfCircle)) {
            return Optional.ofNullable(getSurfaceAndCircleContact(otherBottom, circle))
                    .map(Contact::inverted)
                    .orElse(null);
        }
        return null;
    }

    private static boolean isHalfCircleBottomAndOtherHalfCircleTopContact(Segment bottom, HalfCircle otherHalfCircle) {
        var intersectionPoints = GeometryUtils.getSegmentAndCircleIntersectionPoints(bottom, otherHalfCircle.circle());
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle = otherHalfCircle.center().angleTo(intersectionPoint);
            if (pointAngle > otherHalfCircle.angle() && pointAngle < otherHalfCircle.angle() + Math.PI) {
                return true;
            }
        }
        return false;
    }

    private static Contact getSurfaceAndCircleContact(Segment surface, Circle circle) {
        var projection = GeometryUtils.getPointToSegmentProjection(circle.center(), surface);
        if (projection == null) {
            return null;
        }
        var distance = circle.center().distanceTo(projection);
        var depth = distance < circle.radius() ? circle.radius() - distance : 0.0;
        var normal = circle.center().vectorTo(projection).normalized();
        return Contact.of(depth, normal, projection);
    }
}
