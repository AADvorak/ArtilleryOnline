package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Constants;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.HalfCircle;
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
        while (i < groundIndexes.size()) {
            var position = BattleUtils.getGroundPosition(groundIndexes.get(i), roomModel);

            Contact bottomContact = null;
            var bottomPoint = bottom.findPointWithX(position.getX());
            if (bottomPoint != null && bottomPoint.getY() < position.getY()) {
                var projection = GeometryUtils.getPointToSegmentProjection(position, bottom);
                if (projection != null) {
                    var depth = position.distanceTo(projection);
                    var normal = position.vectorTo(projection).normalized();
                    if (withMaxDepth) depth -= roomModel.getSpecs().getGroundMaxDepth();
                    bottomContact = Contact.withUncheckedDepthOf(depth, normal, position, "HalfCircle bottom with ground");
                }
            }

            Contact topContact = null;
            var distance = halfCircle.center().distanceTo(position);
            var radiusVector = halfCircle.center().vectorTo(position);
            if (distance < halfCircle.radius() && halfCircleNormal.dotProduct(radiusVector) > 0) {
                var depth = halfCircle.radius() - distance;
                if (withMaxDepth) depth -= roomModel.getSpecs().getGroundMaxDepth();
                topContact = Contact.withUncheckedDepthOf(
                        depth,
                        getGroundAngle(position, groundIndexes.get(i), roomModel),
                        position,
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
            return Contact.of(
                    depth,
                    getGroundAngle(nearestPosition, index, roomModel),
                    nearestPosition
            );
        }
        nearestPosition = BattleUtils.getNearestGroundPosition(circle.center().getX(), roomModel);
        var depth = getGroundDepth(circle, nearestPosition.getY());
        if (withMaxDepth) depth -= roomModel.getSpecs().getGroundMaxDepth();
        return Contact.of(
                depth,
                0.0,
                nearestPosition
        );
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
