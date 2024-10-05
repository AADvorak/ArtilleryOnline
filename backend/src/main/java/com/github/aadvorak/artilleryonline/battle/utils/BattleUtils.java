package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.specs.RoomSpecs;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BattleUtils {

    public static double getRoomWidth(RoomSpecs roomSpecs) {
        return roomSpecs.getRightTop().getX() - roomSpecs.getLeftBottom().getX();
    }

    public static double getRoomHeight(RoomSpecs roomSpecs) {
        return roomSpecs.getRightTop().getY() - roomSpecs.getLeftBottom().getY();
    }

    public static List<Integer> getGroundIndexesBetween(double xMin, double xMax,  RoomModel roomModel) {
        var roomWidth = getRoomWidth(roomModel.getSpecs());
        var groundPointsNumber = roomModel.getState().getGroundLine().size();
        var minGroundIndex = (int) Math.ceil((double) groundPointsNumber * xMin / roomWidth);
        var maxGroundIndex = (int) Math.floor((double) groundPointsNumber * xMax / roomWidth);
        if (minGroundIndex < 0) {
            minGroundIndex = 0;
        }
        if (maxGroundIndex >= groundPointsNumber) {
            maxGroundIndex = groundPointsNumber - 1;
        }
        return IntStream.range(minGroundIndex, maxGroundIndex)
                .boxed()
                .collect(Collectors.toList());
    }

    public static Position getNearestGroundPosition(double x, RoomModel roomModel) {
        var roomWidth = getRoomWidth(roomModel.getSpecs());
        var groundPointsNumber = roomModel.getState().getGroundLine().size();
        var objectPositionIndex = (double) groundPointsNumber * x / roomWidth;
        var nearestGroundIndex = (int) Math.floor(objectPositionIndex);
        if (nearestGroundIndex < 0) {
            nearestGroundIndex = 0;
        }
        if (nearestGroundIndex >= groundPointsNumber) {
            nearestGroundIndex = groundPointsNumber - 1;
        }
        return getGroundPosition(nearestGroundIndex, roomModel);
    }

    public static Position getGroundPosition(int index, RoomModel roomModel) {
        return new Position()
                .setX((double) index * roomModel.getSpecs().getStep())
                .setY(roomModel.getState().getGroundLine().get(index));
    }

    public static boolean isLineCrossingCircle(Position lineBegin, Position lineEnd,
                                               Position circleCenter, double circleRadius) {
        var lineA = (lineBegin.getY() - lineEnd.getY()) / (lineBegin.getX() - lineEnd.getX());
        var lineB = lineEnd.getY() - lineA * lineEnd.getX();
        var circleA = 1 + Math.pow(lineA, 2);
        var circleB = 2 * lineA * (lineB - circleCenter.getY()) - 2 * circleCenter.getX();
        var circleC = Math.pow(circleCenter.getX(), 2) - Math.pow(circleRadius, 2)
                + Math.pow(lineB - circleCenter.getY(), 2);
        var discriminant = Math.pow(circleB, 2) - 4 * circleA * circleC;
        if (discriminant < 0) {
            return false;
        }
        var x1 = (- circleB + Math.sqrt(discriminant)) / (2 * circleA);
        var x2 = (- circleB - Math.sqrt(discriminant)) / (2 * circleA);
        var xMin = Math.min(lineBegin.getX(), lineEnd.getX());
        var xMax = Math.max(lineBegin.getX(), lineEnd.getX());
        return x1 <= xMax && x1 >= xMin || x2 <= xMax && x2 >= xMin;
    }

    public static double generateRandom(double min, double max) {
        return min + (Math.random() * (max - min));
    }

    public static double gaussian(double x, double sigma, double mu) {
        return (1.0 / (sigma * Math.sqrt(2 * Math.PI))) * Math.exp(-0.5 * Math.pow((x - mu) / sigma, 2.0));
    }
}
