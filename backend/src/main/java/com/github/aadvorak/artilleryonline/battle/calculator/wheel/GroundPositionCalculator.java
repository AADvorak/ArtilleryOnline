package com.github.aadvorak.artilleryonline.battle.calculator.wheel;

import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.NearestGroundPoint;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class GroundPositionCalculator {

    public static void calculate(WheelCalculations wheelCalculations, double wheelRadius, RoomModel roomModel) {
        var nearestGroundPointByX = BattleUtils.getNearestGroundPosition(
                wheelCalculations.getPosition().getX(), roomModel);
        wheelCalculations.setNearestGroundPointByX(nearestGroundPointByX);
        var nearestGroundPoint = getNearestGroundPoint(wheelCalculations.getPosition(), wheelRadius,
                roomModel, wheelCalculations.getSign().getValue());
        if (nearestGroundPoint == null) {
            wheelCalculations.setGroundAngle(0.0);
            wheelCalculations.setGroundDepth(getGroundDepth(wheelCalculations.getPosition(),
                    wheelRadius, nearestGroundPointByX));
            return;
        }
        wheelCalculations.setNearestGroundPoint(nearestGroundPoint);
        wheelCalculations.setGroundAngle(getGroundAngle(wheelCalculations.getPosition(), nearestGroundPoint, roomModel));
        wheelCalculations.setGroundDepth(getGroundDepth(wheelCalculations.getPosition(), wheelRadius, nearestGroundPoint));
    }

    public static void calculateNext(WheelCalculations wheelCalculations, RoomModel roomModel) {
        var wheelRadius = wheelCalculations.getVehicle().getModel().getSpecs().getWheelRadius();
        var nextPosition = wheelCalculations.getNext().getPosition();
        var nearestGroundPointByX = BattleUtils.getNearestGroundPosition(nextPosition.getX(), roomModel);
        wheelCalculations.getNext().setNearestGroundPointByX(nearestGroundPointByX);
        var nearestGroundPoint = getNearestGroundPoint(nextPosition, wheelRadius,
                roomModel, wheelCalculations.getSign().getValue());
        if (nearestGroundPoint == null) {
            wheelCalculations.getNext().setGroundAngle(0.0);
            wheelCalculations.getNext().setGroundDepth(getGroundDepth(nextPosition, wheelRadius, nearestGroundPointByX));
            return;
        }
        wheelCalculations.getNext().setNearestGroundPoint(nearestGroundPoint);
        wheelCalculations.getNext().setGroundAngle(getGroundAngle(nextPosition, nearestGroundPoint, roomModel));
        wheelCalculations.getNext().setGroundDepth(getGroundDepth(nextPosition, wheelRadius, nearestGroundPoint));
    }

    public static void calculateNext(DroneCalculations drone, RoomModel roomModel) {
        var hullRadius = drone.getModel().getSpecs().getHullRadius();
        var nextPosition = drone.getNext().getPosition().getCenter();
        var nearestGroundPointByX = BattleUtils.getNearestGroundPosition(nextPosition.getX(), roomModel);
        drone.getNext().setNearestGroundPointByX(nearestGroundPointByX);
        var nearestGroundPoint = getNearestGroundPoint(nextPosition, hullRadius,
                roomModel, 1);
        if (nearestGroundPoint == null) {
            drone.getNext().setGroundAngle(0.0);
            drone.getNext().setGroundDepth(getGroundDepth(nextPosition, hullRadius, nearestGroundPointByX));
            return;
        }
        drone.getNext().setNearestGroundPoint(nearestGroundPoint);
        drone.getNext().setGroundAngle(getGroundAngle(nextPosition, nearestGroundPoint, roomModel));
        drone.getNext().setGroundDepth(getGroundDepth(nextPosition, hullRadius, nearestGroundPoint));
    }

    private static double getGroundAngle(Position position, NearestGroundPoint nearestGroundPoint, RoomModel roomModel) {
        if (nearestGroundPoint.index() > 0 && nearestGroundPoint.position().getX() <= position.getX()) {
            var otherGroundPosition = BattleUtils.getGroundPosition(nearestGroundPoint.index() - 1, roomModel);
            return otherGroundPosition.angleTo(nearestGroundPoint.position());
        } else {
            var otherGroundPosition = BattleUtils.getGroundPosition(nearestGroundPoint.index() + 1, roomModel);
            return nearestGroundPoint.position().angleTo(otherGroundPosition);
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

    private static double getGroundDepth(Position position, double radius, NearestGroundPoint nearestGroundPoint) {
        if (nearestGroundPoint.position().getY() <= position.getY()) {
            return radius - nearestGroundPoint.distance();
        } else {
            return radius + nearestGroundPoint.distance();
        }
    }

    private static double getGroundDepth(Position position, double radius, Position nearestGroundPointByX) {
        var depth = nearestGroundPointByX.getY() - position.getY() + radius;
        if (depth < 0) {
            return 0.0;
        }
        return depth;
    }
}
