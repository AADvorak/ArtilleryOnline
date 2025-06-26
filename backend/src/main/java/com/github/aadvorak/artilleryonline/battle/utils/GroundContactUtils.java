package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Constants;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.HalfCircle;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;
import com.github.aadvorak.artilleryonline.battle.common.lines.Trapeze;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GroundContactUtils {

    public static Set<Contact> getGroundContacts(HalfCircle halfCircle, RoomModel roomModel, boolean withMaxDepth) {
        var groundIndexes = BattleUtils.getGroundIndexesBetween(halfCircle.center().getX() - halfCircle.radius(),
                halfCircle.center().getX() + halfCircle.radius(), roomModel);
        var contacts = new HashSet<Contact>();
        if (groundIndexes.isEmpty()) {
            return contacts;
        }
        var bottom = halfCircle.chord();
        var i = 0;
        var halfCircleNormal = halfCircle.center().vectorTo(halfCircle.center()
                .shifted(1.0, halfCircle.angle() + Math.PI / 2));
        var maxDepth = withMaxDepth ? roomModel.getSpecs().getGroundMaxDepth() : 0.0;
        while (i < groundIndexes.size()) {
            var position = BattleUtils.getGroundPosition(groundIndexes.get(i), roomModel);

            Contact bottomContact = getGroundContact(bottom, position, maxDepth, "HalfCircle bottom with ground");

            Contact topContact = null;
            var distance = halfCircle.center().distanceTo(position);
            var radiusVector = halfCircle.center().vectorTo(position);
            if (distance < halfCircle.radius() && halfCircleNormal.dotProduct(radiusVector) > 0) {
                var depth = halfCircle.radius() - distance - maxDepth;
                var contactPosition = halfCircle.center().shifted(halfCircle.radius(),
                        halfCircle.center().angleTo(position));
                topContact = Contact.withUncheckedDepthOf(
                        depth,
                        getGroundAngle(position, groundIndexes.get(i), roomModel),
                        contactPosition,
                        "HalfCircle top with ground"
                );
            }

            Contact resultContact = null;
            if (topContact != null && bottomContact != null) {
                resultContact = topContact.depth() < bottomContact.depth() ? topContact : bottomContact;
            } else if (topContact != null) {
                resultContact = topContact;
            }  else if (bottomContact != null) {
                resultContact = bottomContact;
            }

            if (resultContact != null && resultContact.depth() >= Constants.INTERPENETRATION_THRESHOLD) {
                contacts.add(resultContact);
            }

            i++;
        }
        return contacts;
    }

    public static Set<Contact> getGroundContacts(Trapeze trapeze, RoomModel roomModel, boolean withMaxDepth) {
        var maxDistance = trapeze.maxDistanceFromCenter();
        var groundIndexes = BattleUtils.getGroundIndexesBetween(trapeze.position().getX() - maxDistance,
                trapeze.position().getX() + maxDistance, roomModel);
        var contacts = new HashSet<Contact>();
        if (groundIndexes.isEmpty()) {
            return contacts;
        }
        var i = 0;
        var maxDepth = withMaxDepth ? roomModel.getSpecs().getGroundMaxDepth() : 0.0;
        var bottom = trapeze.bottom();
        var top = trapeze.top();
        var right = trapeze.right();
        var left = trapeze.left();
        while (i < groundIndexes.size()) {
            var position = BattleUtils.getGroundPosition(groundIndexes.get(i), roomModel);

            var bottomContact = getGroundContact(bottom, position, maxDepth, "Trapeze bottom with ground");
            var topContact = getGroundContact(top, position, maxDepth, "Trapeze top with ground");
            var rightContact = getGroundContact(right, position, maxDepth,  "Trapeze right with ground");
            var leftContact = getGroundContact(left, position, maxDepth, "Trapeze left with ground");

            Contact resultContact = null;
            if (topContact != null) {
                resultContact = topContact;
            }
            if (bottomContact != null && (resultContact == null || bottomContact.depth() < resultContact.depth())) {
                resultContact = bottomContact;
            }
            if (rightContact != null && (resultContact == null || rightContact.depth() < resultContact.depth())) {
                resultContact = rightContact;
            }
            if (leftContact != null && (resultContact == null || leftContact.depth() < resultContact.depth())) {
                resultContact = leftContact;
            }

            if (resultContact != null && resultContact.depth() >= Constants.INTERPENETRATION_THRESHOLD) {
                contacts.add(resultContact);
            }
            i++;
        }
        return contacts;
    }

    public static Contact getGroundContact(Circle circle, RoomModel roomModel, boolean withMaxDepth) {
        var groundIndexes = BattleUtils.getGroundIndexesBetween(circle.center().getX() - circle.radius(),
                circle.center().getX() + circle.radius(), roomModel);
        if (groundIndexes.isEmpty()) {
            return null;
        }
        Position nearestPosition = null;
        Double minimalDistance = null;
        Integer index = null;
        var i = 0;
        while (i < groundIndexes.size()) {
            var position = BattleUtils.getGroundPosition(groundIndexes.get(i), roomModel);
            var distance = position.distanceTo(circle.center());
            if (distance < circle.radius()) {
                if (minimalDistance == null || distance < minimalDistance) {
                    nearestPosition = position;
                    minimalDistance = distance;
                    index = groundIndexes.get(i);
                }
            }
            i++;
        }
        if (nearestPosition != null) {
            var depth = getGroundDepth(circle, nearestPosition.getY(), minimalDistance);
            if (withMaxDepth) depth -= roomModel.getSpecs().getGroundMaxDepth();
            var contactPosition = circle.center().shifted(circle.radius(),
                    circle.center().angleTo(nearestPosition));
            return Contact.of(
                    depth,
                    getGroundAngle(nearestPosition, index, roomModel),
                    contactPosition
            );
        }
        nearestPosition = BattleUtils.getNearestGroundPosition(circle.center().getX(), roomModel);
        var depth = getGroundDepth(circle, nearestPosition.getY());
        if (withMaxDepth) depth -= roomModel.getSpecs().getGroundMaxDepth();
        return Contact.of(
                depth,
                0.0,
                circle.center().shifted(circle.radius(), -Math.PI / 2)
        );
    }

    private static Contact getGroundContact(Segment segment, Position groundPosition, double maxDepth, String description) {
        var segmentPoint = segment.findPointWithX(groundPosition.getX());
        if (segmentPoint != null && segmentPoint.getY() < groundPosition.getY()) {
            var projection = GeometryUtils.getPointToSegmentProjection(groundPosition, segment);
            if (projection != null) {
                var depth = groundPosition.distanceTo(projection) - maxDepth;
                var normal = groundPosition.vectorTo(projection).normalized();
                return Contact.withUncheckedDepthOf(depth, normal, projection, description);
            }
        }
        return null;
    }

    private static double getGroundDepth(Circle circle, double y, double distance) {
        if (y <= circle.center().getY()) {
            return circle.radius() - distance;
        } else {
            return circle.radius() + distance;
        }
    }

    private static double getGroundDepth(Circle circle, double y) {
        var depth = y - circle.center().getY() + circle.radius();
        if (depth < 0) {
            return 0.0;
        }
        return depth;
    }

    private static double getGroundAngle(Position groundPosition, int groundIndex, RoomModel roomModel) {
        var angles = new ArrayList<Double>();
        var groundPositionBefore = BattleUtils.getGroundPosition(groundIndex - 1, roomModel);
        if  (groundPositionBefore != null) {
            angles.add(groundPositionBefore.angleTo(groundPosition));
        }
        var groundPositionAfter = BattleUtils.getGroundPosition(groundIndex + 1, roomModel);
        if (groundPositionAfter != null) {
            angles.add(groundPosition.angleTo(groundPositionAfter));
        }
        return angles.stream().reduce(0.0, Double::sum) / angles.size();
    }
}
