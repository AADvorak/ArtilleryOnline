package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.*;

import java.util.*;

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
            if (GeometryUtils.isPointLyingOnArc(pointAngle, otherHalfCircle.angle(),
                    otherHalfCircle.angle() + Math.PI)) {
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
            if (GeometryUtils.isPointLyingOnArc(pointAngle1, halfCircle.angle(),
                    halfCircle.angle() + Math.PI)
                    && GeometryUtils.isPointLyingOnArc(pointAngle2, otherHalfCircle.angle(),
                    otherHalfCircle.angle() + Math.PI)) {
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

    public static Contact getTrapezesContact(Trapeze trapeze, Trapeze otherTrapeze) {
        var maxDistance = trapeze.maxDistanceFromCenter();
        var otherMaxDistance = otherTrapeze.maxDistanceFromCenter();
        if (trapeze.position().getCenter().distanceTo(otherTrapeze.position().getCenter())
                > maxDistance + otherMaxDistance) {
            return null;
        }
        var polygon = new Polygon(trapeze);

        Map<Segment, Set<Segment>> otherSidesIntersections = new HashMap<>();
        otherSidesIntersections.put(otherTrapeze.bottom(), new HashSet<>());
        otherSidesIntersections.put(otherTrapeze.right(), new HashSet<>());
        otherSidesIntersections.put(otherTrapeze.top(), new HashSet<>());
        otherSidesIntersections.put(otherTrapeze.left(), new HashSet<>());

        otherSidesIntersections.forEach((otherSide, intersections) ->
                polygon.sides().forEach(side -> {
                    if (GeometryUtils.getSegmentsIntersectionPoint(otherSide, side) != null) {
                        intersections.add(side);
                    }
                })
        );

        for (var otherSide : otherSidesIntersections.keySet()) {
            var intersections = otherSidesIntersections.get(otherSide);
            if (intersections.size() == 2) {
                var iterator = intersections.iterator();
                var firstIntersection = iterator.next();
                var secondIntersection = iterator.next();
                if (polygon.next(firstIntersection).equals(secondIntersection)) {
                    return getContact(firstIntersection.end(), otherSide, false);
                } else if (polygon.next(secondIntersection).equals(firstIntersection)) {
                    return getContact(secondIntersection.end(), otherSide, false);
                } else {
                    var contacts = new ArrayList<Contact>();
                    contacts.add(getContact(firstIntersection.end(), otherSide, true));
                    contacts.add(getContact(secondIntersection.end(), otherSide, true));
                    contacts.add(getContact(firstIntersection.begin(), otherSide, true));
                    contacts.add(getContact(secondIntersection.begin(), otherSide, true));
                    var contactsIterator = contacts.iterator();
                    var contact = contactsIterator.next();
                    while (contactsIterator.hasNext()) {
                        var otherContact = contactsIterator.next();
                        if (otherContact != null && (contact == null || otherContact.depth() > contact.depth())) {
                            contact = otherContact;
                        }
                    }
                    return contact;
                }
            }
        }

        return null;
    }

    private static Contact getContact(Position position, Segment segment, boolean checkSide) {
        var projection = GeometryUtils.getPointToLineProjection(position, segment);
        var normal = segment.normal();
        if (checkSide) {
            var direction = projection.vectorTo(position).normalized();
            if (direction.dotProduct(normal) < 0) {
                return null;
            }
        }
        return Contact.of(position.distanceTo(projection), normal, new Segment(position, projection).center());
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
