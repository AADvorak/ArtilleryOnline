package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;

public class GroundContactUtils {

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
        while (i >= 0 && i < groundIndexes.size()) {
            var position = BattleUtils.getGroundPosition(groundIndexes.get(i), roomModel);
            var distance = position.distanceTo(circle.center());
            if (distance <= circle.radius()) {
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
                    getGroundAngle(circle.center(), nearestPosition, index, roomModel),
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

    private static double getGroundAngle(Position position, Position groundPosition, int groundIndex, RoomModel roomModel) {
        if (groundIndex > 0 && groundPosition.getX() <= position.getX()) {
            var otherGroundPosition = BattleUtils.getGroundPosition(groundIndex - 1, roomModel);
            return otherGroundPosition.angleTo(groundPosition);
        } else {
            var otherGroundPosition = BattleUtils.getGroundPosition(groundIndex + 1, roomModel);
            return groundPosition.angleTo(otherGroundPosition);
        }
    }
}
