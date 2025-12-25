package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.*;
import com.github.aadvorak.artilleryonline.battle.contact.PolygonsContactDetector;
import com.github.aadvorak.artilleryonline.battle.contact.SATPolygonsContactDetector;

import java.util.*;

public class ContactUtils {

    public static Contact getBodyPartsContact(BodyPart bodyPart, BodyPart otherBodyPart) {
        return getBodyPartsContact(bodyPart, otherBodyPart, new SATPolygonsContactDetector());
    }

    public static Contact getBodyPartsContact(BodyPart bodyPart, BodyPart otherBodyPart, PolygonsContactDetector detector) {
        if (bodyPart instanceof Circle circle) {
            if (otherBodyPart instanceof Circle otherCircle) {
                return getCirclesContact(circle, otherCircle);
            }
            if (otherBodyPart instanceof HalfCircle otherHalfCircle) {
                return getCircleHalfCircleContact(circle, otherHalfCircle);
            }
            if (otherBodyPart instanceof Trapeze otherTrapeze) {
                return Optional.ofNullable(getTrapezeCircleContact(otherTrapeze, circle))
                        .map(Contact::inverted).orElse(null);
            }
        }
        if (bodyPart instanceof HalfCircle halfCircle) {
            if (otherBodyPart instanceof Circle otherCircle) {
                return Optional.ofNullable(getCircleHalfCircleContact(otherCircle, halfCircle))
                        .map(Contact::inverted).orElse(null);
            }
            if (otherBodyPart instanceof HalfCircle otherHalfCircle) {
                return getHalfCirclesContact(halfCircle, otherHalfCircle);
            }
            if (otherBodyPart instanceof Trapeze otherTrapeze) {
                return Optional.ofNullable(getTrapezeHalfCircleContact(otherTrapeze, halfCircle))
                        .map(Contact::inverted).orElse(null);
            }
        }
        if (bodyPart instanceof Trapeze trapeze) {
            if (otherBodyPart instanceof Circle otherCircle) {
                return getTrapezeCircleContact(trapeze, otherCircle);
            }
            if (otherBodyPart instanceof HalfCircle otherHalfCircle) {
                return getTrapezeHalfCircleContact(trapeze, otherHalfCircle);
            }
            if (otherBodyPart instanceof Trapeze otherTrapeze) {
                return getTrapezesContact(trapeze, otherTrapeze, detector);
            }
        }
        return null;
    }

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
        return Optional.ofNullable(getSegmentAndCircleContact(otherBottom, circle))
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
            return getSegmentAndCircleContact(bottom, otherCircle);
        }
        var otherBottom = otherHalfCircle.chord();
        if (isHalfCircleBottomAndOtherHalfCircleTopContact(otherBottom, halfCircle)) {
            return Optional.ofNullable(getSegmentAndCircleContact(otherBottom, circle))
                    .map(Contact::inverted)
                    .orElse(null);
        }
        return null;
    }

    public static Contact getTrapezeCircleContact(Trapeze trapeze, Circle circle) {
        if (trapeze.position().getCenter().distanceTo(circle.center())
                > trapeze.maxDistanceFromCenter() + circle.radius()) {
            return null;
        }
        var edges = Set.of(trapeze.bottomLeft(), trapeze.bottomRight(), trapeze.topLeft(), trapeze.topRight());
        for (var edge : edges) {
            var contact = getPointAndCircleContact(edge, circle);
            if (contact != null) {
                return contact;
            }
        }
        var polygon = new Polygon(trapeze);
        for (var side : polygon.sides()) {
            var contact = getSegmentAndCircleContact(side, circle);
            if (contact != null) {
                return contact;
            }
        }
        Double minInnerDistance = null;
        Segment closestSide = null;
        for (var side : polygon.sides()) {
            var distance = innerDistanceFromPointToSegment(circle.center(), side);
            if (distance == 0.0) {
                return null;
            }
            if (minInnerDistance == null || distance < minInnerDistance) {
                minInnerDistance = distance;
                closestSide = side;
            }
        }
        return Contact.of(minInnerDistance + circle.radius(),
                closestSide.normal().inverted(), circle.center());
    }

    public static Contact getTrapezeHalfCircleContact(Trapeze trapeze, HalfCircle halfCircle) {
        if (trapeze.position().getCenter().distanceTo(halfCircle.center())
                > trapeze.maxDistanceFromCenter() + halfCircle.radius()) {
            return null;
        }
        var polygon = new Polygon(trapeze);
        var halfCircleTopIntersections = new HashSet<Segment>();
        var halfCircleBottomIntersections = new HashSet<Segment>();
        var halfCircleBottom = halfCircle.chord();
        var halfCircleBottomLeft = halfCircle.bottomLeft();
        var halfCircleBottomRight = halfCircle.bottomRight();
        var circle = halfCircle.circle();
        for (var side : polygon.sides()) {
            if (GeometryUtils.getSegmentsIntersectionPoint(side, halfCircleBottom) != null) {
                halfCircleBottomIntersections.add(side);
            }
            GeometryUtils.getSegmentAndHalfCircleIntersectionPoints(side, halfCircle)
                    .forEach(point -> halfCircleTopIntersections.add(side));
        }
        if (halfCircleTopIntersections.size() == 2) {
            var iterator = halfCircleTopIntersections.iterator();
            var firstSide = iterator.next();
            var secondSide = iterator.next();
            if (polygon.next(firstSide).equals(secondSide)) {
                return getPointAndCircleContact(firstSide.end(), circle);
            } else if (polygon.next(secondSide).equals(firstSide)) {
                return getPointAndCircleContact(secondSide.end(), circle);
            } else {
                var contacts = new HashSet<Contact>();
                contacts.add(getPointAndCircleContact(firstSide.end(), circle));
                contacts.add(getPointAndCircleContact(secondSide.end(), circle));
                contacts.add(getPointAndCircleContact(firstSide.begin(), circle));
                contacts.add(getPointAndCircleContact(secondSide.begin(), circle));
                contacts.add(getPointAndCircleContact(polygon.next(firstSide).center(), circle));
                contacts.add(getPointAndCircleContact(polygon.next(secondSide).center(), circle));
                return getDeepestContact(contacts);
            }
        }
        if (halfCircleBottomIntersections.size() == 2) {
            var iterator = halfCircleBottomIntersections.iterator();
            var firstSide = iterator.next();
            var secondSide = iterator.next();
            if (polygon.next(firstSide).equals(secondSide)) {
                return getPointAndSegmentContact(firstSide.end(), halfCircleBottom, false);
            } else if (polygon.next(secondSide).equals(firstSide)) {
                return getPointAndSegmentContact(secondSide.end(), halfCircleBottom, false);
            } else {
                var contacts = new HashSet<Contact>();
                contacts.add(getPointAndSegmentContact(firstSide.end(), halfCircleBottom, true));
                contacts.add(getPointAndSegmentContact(secondSide.end(), halfCircleBottom, true));
                contacts.add(getPointAndSegmentContact(firstSide.begin(), halfCircleBottom, true));
                contacts.add(getPointAndSegmentContact(secondSide.begin(), halfCircleBottom, true));
                return getDeepestContact(contacts);
            }
        }
        if (halfCircleTopIntersections.size() == 1 && halfCircleBottomIntersections.size() == 1) {
            var firstSide = halfCircleTopIntersections.iterator().next();
            var secondSide = halfCircleBottomIntersections.iterator().next();
            if (firstSide.equals(secondSide)) {
                var contact = getPointAndSegmentContact(halfCircleBottomLeft, firstSide, true);
                if (contact != null) {
                    return contact.inverted();
                }
                contact = getPointAndSegmentContact(halfCircleBottomRight, firstSide, true);
                if (contact != null) {
                    return contact.inverted();
                }
            } else {
                Position otherEdge = null;
                if (isPointOnInnerSideOfSegment(halfCircleBottomLeft, firstSide)
                        && isPointOnInnerSideOfSegment(halfCircleBottomLeft, secondSide)) {
                    otherEdge = halfCircleBottomLeft;
                }  else if (isPointOnInnerSideOfSegment(halfCircleBottomRight, firstSide)
                        && isPointOnInnerSideOfSegment(halfCircleBottomRight, secondSide)) {
                    otherEdge = halfCircleBottomRight;
                }
                if (otherEdge != null) {
                    Position edge = null;
                    if (polygon.next(firstSide).equals(secondSide)) {
                        edge = firstSide.end();
                    } else if (polygon.next(secondSide).equals(firstSide)) {
                        edge = secondSide.end();
                    }
                    if (edge != null) {
                        return Contact.of(
                                edge.distanceTo(otherEdge),
                                otherEdge.vectorTo(edge).normalized(),
                                new Segment(edge, otherEdge).center()
                        );
                    }
                }
            }
        }
        if (halfCircleTopIntersections.size() == 1) {
            var side = halfCircleTopIntersections.iterator().next();
            return getSegmentAndCircleContact(side, circle);
        }
        return null;
    }

    public static Contact getTrapezesContact(Trapeze trapeze, Trapeze otherTrapeze) {
        return getTrapezesContact(trapeze, otherTrapeze, new SATPolygonsContactDetector());
    }

    private static Contact getTrapezesContact(Trapeze trapeze, Trapeze otherTrapeze, PolygonsContactDetector detector) {
        var maxDistance = trapeze.maxDistanceFromCenter();
        var otherMaxDistance = otherTrapeze.maxDistanceFromCenter();
        if (trapeze.position().getCenter().distanceTo(otherTrapeze.position().getCenter())
                > maxDistance + otherMaxDistance) {
            return null;
        }
        var polygon = new Polygon(trapeze);
        var otherPolygon = new Polygon(otherTrapeze);
        return detector.detect(polygon, otherPolygon);
    }

    private static Contact getPointAndSegmentContact(Position position, Segment segment, boolean checkSide) {
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

    private static boolean isPointOnInnerSideOfSegment(Position position, Segment segment) {
        var projection = GeometryUtils.getPointToLineProjection(position, segment);
        var normal = segment.normal();
        var direction = projection.vectorTo(position).normalized();
        return direction.dotProduct(normal) > 0;
    }

    private static double innerDistanceFromPointToSegment(Position position, Segment segment) {
        var projection = GeometryUtils.getPointToSegmentProjection(position, segment);
        if  (projection == null) {
            return 0.0;
        }
        var normal = segment.normal();
        var direction = projection.vectorTo(position).normalized();
        if  (direction.dotProduct(normal) < 0) {
            return 0.0;
        }
        return position.distanceTo(projection);
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

    private static Contact getSegmentAndCircleContact(Segment surface, Circle circle) {
        var projection = GeometryUtils.getPointToSegmentProjection(circle.center(), surface);
        if (projection == null) {
            return null;
        }
        var distance = projection.distanceTo(circle.center());
        if (distance > circle.radius()) {
            return null;
        }
        var direction = circle.center().vectorTo(projection);
        var surfaceNormal = surface.normal();
        var depth = direction.dotProduct(surfaceNormal) > 0
                ? circle.radius() - distance
                : circle.radius() + distance;
        return Contact.of(depth, surfaceNormal.inverted(), projection);
    }

    private static Contact getPointAndCircleContact(Position point, Circle circle) {
        var distance = circle.center().distanceTo(point);
        var depth = distance < circle.radius() ? circle.radius() - distance : 0.0;
        var normal = point.vectorTo(circle.center()).normalized();
        return Contact.of(depth, normal, point);
    }

    private static Contact getDeepestContact(Set<Contact> contacts) {
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

    public static class IntersectionsPolygonsContactDetector implements PolygonsContactDetector {
        @Override
        public Contact detect(Polygon polygon, Polygon otherPolygon) {
            Map<Segment, Set<Segment>> otherSidesIntersections = new HashMap<>();
            otherPolygon.sides().forEach(otherSide -> otherSidesIntersections.put(otherSide, new HashSet<>()));

            otherSidesIntersections.forEach((otherSide, intersections) ->
                    polygon.sides().forEach(side -> {
                        if (GeometryUtils.getSegmentsIntersectionPoint(otherSide, side) != null) {
                            intersections.add(side);
                        }
                    })
            );

            for (var otherSide : otherPolygon.sides()) {
                var intersections = otherSidesIntersections.get(otherSide);
                if (intersections.size() == 2) {
                    var iterator = intersections.iterator();
                    var firstSide = iterator.next();
                    var secondSide = iterator.next();
                    if (polygon.next(firstSide).equals(secondSide)) {
                        return getPointAndSegmentContact(firstSide.end(), otherSide, false);
                    } else if (polygon.next(secondSide).equals(firstSide)) {
                        return getPointAndSegmentContact(secondSide.end(), otherSide, false);
                    } else {
                        var contacts = new HashSet<Contact>();
                        contacts.add(getPointAndSegmentContact(firstSide.end(), otherSide, true));
                        contacts.add(getPointAndSegmentContact(secondSide.end(), otherSide, true));
                        contacts.add(getPointAndSegmentContact(firstSide.begin(), otherSide, true));
                        contacts.add(getPointAndSegmentContact(secondSide.begin(), otherSide, true));
                        return getDeepestContact(contacts);
                    }
                } else if (intersections.size() == 1) {
                    var firstSide = intersections.iterator().next();
                    var secondOtherSide = otherPolygon.next(otherSide);
                    if (otherSidesIntersections.get(secondOtherSide).size() == 1) {
                        var secondSide = otherSidesIntersections.get(secondOtherSide).iterator().next();
                        // todo why equals is possible?
                        if (firstSide.equals(secondSide)) {
                            return null;
                        }
                        var edge = polygon.next(firstSide).equals(secondSide) ? firstSide.end() : secondSide.end();
                        var otherEdge = otherSide.end();
                        var edgeOtherSideProjection = GeometryUtils.getPointToLineProjection(edge, otherSide);
                        var edgeOtherSideDistance = edge.distanceTo(edgeOtherSideProjection);
                        var edgeSecondOtherSideProjection = GeometryUtils.getPointToLineProjection(edge, secondOtherSide);
                        var edgeSecondOtherSideDistance = edge.distanceTo(edgeSecondOtherSideProjection);
                        var edgeOtherEdgeDistance = edge.distanceTo(otherEdge);
                        if (edgeOtherEdgeDistance < 2 * edgeOtherSideDistance && edgeOtherEdgeDistance < 2 * edgeSecondOtherSideDistance) {
                            return Contact.of(
                                    edgeOtherEdgeDistance,
                                    otherEdge.vectorTo(edge).normalized(),
                                    new Segment(edge, otherEdge).center()
                            );
                        } else if (edgeOtherSideDistance < edgeSecondOtherSideDistance) {
                            return Contact.of(
                                    edgeOtherSideDistance,
                                    edgeOtherSideProjection.vectorTo(edge).normalized(),
                                    new Segment(edge, edgeOtherSideProjection).center()
                            );
                        } else {
                            return Contact.of(
                                    edgeSecondOtherSideDistance,
                                    edgeSecondOtherSideProjection.vectorTo(edge).normalized(),
                                    new Segment(edge, edgeSecondOtherSideProjection).center()
                            );
                        }
                    }
                }
            }

            return null;
        }
    }
}
