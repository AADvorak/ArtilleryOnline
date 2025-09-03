package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.specs.RoomSpecs;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BattleUtils {

    public static boolean positionIsOutOfRoom(Position position, RoomSpecs roomSpecs) {
        var xMax = roomSpecs.getRightTop().getX();
        var xMin = roomSpecs.getLeftBottom().getX();
        return position.getX() >= xMax || position.getX() <= xMin;
    }

    public static double getRoomWidth(RoomSpecs roomSpecs) {
        return roomSpecs.getRightTop().getX() - roomSpecs.getLeftBottom().getX();
    }

    public static double getRoomHeight(RoomSpecs roomSpecs) {
        return roomSpecs.getRightTop().getY() - roomSpecs.getLeftBottom().getY();
    }

    public static Position getFirstPointUnderGround(Segment segment, RoomModel roomModel) {
        var xMin = Math.min(segment.begin().getX(), segment.end().getX());
        var xMax = Math.max(segment.begin().getX(), segment.end().getX());
        var indexes = getGroundIndexesBetween(xMin, xMax, roomModel);
        if (indexes.isEmpty()) {
            var beginNearestPosition = getNearestGroundPosition(segment.begin().getX(), roomModel);
            var endNearestPosition = getNearestGroundPosition(segment.end().getX(), roomModel);
            if (beginNearestPosition.getY() > segment.begin().getY()) {
                return beginNearestPosition;
            }
            if (endNearestPosition.getY() > segment.end().getY()) {
                return endNearestPosition;
            }
            return null;
        }
        var start = segment.begin().getX() < segment.end().getX() ? 0 : indexes.size() - 1;
        var increment = segment.begin().getX() < segment.end().getX() ? 1 : -1;
        for (var index = start; index >= 0 && index < indexes.size(); index += increment) {
            var groundPosition = getGroundPosition(indexes.get(index), roomModel);
            var segmentPosition = segment.findPointWithX(groundPosition.getX());
            if (segmentPosition != null && groundPosition.getY() > segmentPosition.getY()) {
                return segmentPosition;
            }
        }
        return null;
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

    public static boolean positionIsUnderGround(Position position, RoomModel roomModel) {
        var nearestGroundPosition = BattleUtils.getNearestGroundPosition(position.getX(), roomModel);
        return position.getY() <= nearestGroundPosition.getY();
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
        var groundLine = roomModel.getState().getGroundLine();
        if (index < 0 || index >= groundLine.size()) {
            return null;
        }
        var y = groundLine.get(index);
        if (y == null) {
            return null;
        }
        return new Position()
                .setX((double) index * roomModel.getSpecs().getStep())
                .setY(y);
    }

    public static double generateRandom(double min, double max) {
        return min + (Math.random() * (max - min));
    }

    public static int generateRandom(int min, int max) {
        return (int) (min + (Math.random() * (max - min)));
    }

    public static double gaussian(double x, double sigma, double mu) {
        return (1.0 / (sigma * Math.sqrt(2 * Math.PI))) * Math.exp(-0.5 * Math.pow((x - mu) / sigma, 2.0));
    }

    public static void gaussianFilter(List<Double> input, List<Integer> smoothIndexes) {
        int windowSize = Math.min(smoothIndexes.size(), 20);
        double[] kernel = new double[windowSize];
        double sigma = windowSize / 6.0;
        double sum = 0;
        // Create Gaussian kernel
        for (int i = 0; i < windowSize; i++) {
            int x = i - windowSize / 2;
            kernel[i] = Math.exp(-(x * x) / (2 * sigma * sigma));
            sum += kernel[i];
        }
        // Normalize kernel
        for (int i = 0; i < windowSize; i++) {
            kernel[i] /= sum;
        }
        // Apply filter
        for (var i : smoothIndexes) {
            double smoothedValue = 0;
            for (int j = 0; j < windowSize; j++) {
                int index = i + j - windowSize / 2;
                if (index >= 0 && index < input.size()) {
                    smoothedValue += input.get(index) * kernel[j];
                }
            }
            input.set(i, smoothedValue);
        }
    }
}
