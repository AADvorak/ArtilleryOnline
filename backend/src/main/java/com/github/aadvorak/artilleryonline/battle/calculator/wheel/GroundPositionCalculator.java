package com.github.aadvorak.artilleryonline.battle.calculator.wheel;

import com.github.aadvorak.artilleryonline.battle.calculations.NearestGroundPoint;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class GroundPositionCalculator {

    public static void calculate(WheelCalculations wheelCalculations, double wheelRadius, RoomModel roomModel) {
        wheelCalculations.setNearestGroundPointByX(BattleUtils.getNearestGroundPosition(
                wheelCalculations.getPosition().getX(), roomModel));
        var nearestGroundPoint = getNearestGroundPoint(wheelCalculations.getPosition(), wheelRadius,
                roomModel, wheelCalculations.getSign().getValue());
        if (nearestGroundPoint == null) {
            return;
        }
        wheelCalculations.setNearestGroundPoint(nearestGroundPoint);
        wheelCalculations.setGroundAngle(getGroundAngle(wheelCalculations.getPosition(), nearestGroundPoint, roomModel));
        if (nearestGroundPoint.position().getY() <= wheelCalculations.getPosition().getY()) {
            wheelCalculations.setGroundDepth(wheelRadius - nearestGroundPoint.distance());
        } else {
            wheelCalculations.setGroundDepth(wheelRadius + nearestGroundPoint.distance());
        }
    }

    private static double getGroundAngle(Position position, NearestGroundPoint nearestGroundPoint, RoomModel roomModel) {
        if (nearestGroundPoint.index() > 0 && nearestGroundPoint.position().getX() <= position.getX()) {
            var otherGroundPosition = BattleUtils.getGroundPosition(nearestGroundPoint.index() - 1, roomModel);
            return Math.atan((nearestGroundPoint.position().getY() - otherGroundPosition.getY())
                    / (nearestGroundPoint.position().getX() - otherGroundPosition.getX()));
        } else {
            var otherGroundPosition = BattleUtils.getGroundPosition(nearestGroundPoint.index() + 1, roomModel);
            return Math.atan((otherGroundPosition.getY() - nearestGroundPoint.position().getY())
                    / (otherGroundPosition.getX() - nearestGroundPoint.position().getX()));
        }
    }

    private static NearestGroundPoint getNearestGroundPoint(Position objectPosition, double objectRadius,
                                                            RoomModel roomModel, int sign) {
        var groundIndexes = BattleUtils.getGroundIndexesBetween(objectPosition.getX() - objectRadius,
                objectPosition.getX() + objectRadius, roomModel);
        if (groundIndexes.isEmpty()) {
            return null;
        }
        Position nearestPosition = null;
        Double minimalDistance = null;
        Integer index = null;
        var i = sign > 0 ? 0 : groundIndexes.size() - 1;
        while (i >= 0 && i < groundIndexes.size()) {
            var position = BattleUtils.getGroundPosition(groundIndexes.get(i), roomModel);
            var distance = position.distanceTo(objectPosition);
            if (distance <= objectRadius) {
                if (minimalDistance == null || distance < minimalDistance) {
                    nearestPosition = position;
                    minimalDistance = distance;
                    index = groundIndexes.get(i);
                }
            }
            i += sign;
        }
        if (nearestPosition == null) {
            return null;
        }
        return new NearestGroundPoint(nearestPosition, minimalDistance, index);
    }
}
