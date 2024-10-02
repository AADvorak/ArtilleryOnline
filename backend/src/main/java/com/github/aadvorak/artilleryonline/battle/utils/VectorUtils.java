package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Vector;

public class VectorUtils {
    
    public static double getVerticalProjection(Vector vector, double groundAngle) {
        return - vector.getX() * Math.sin(groundAngle) + vector.getY() * Math.cos(groundAngle);
    }

    public static double getHorizontalProjection(Vector vector, double groundAngle) {
        return vector.getX() * Math.cos(groundAngle) + vector.getY() * Math.sin(groundAngle);
    }

    public static double getComponentX(double verticalProjection, double horizontalProjection, double groundAngle) {
        return - verticalProjection * Math.sin(groundAngle) + horizontalProjection * Math.cos(groundAngle);
    }

    public static double getComponentY(double verticalProjection, double horizontalProjection, double groundAngle) {
        return verticalProjection * Math.cos(groundAngle) + horizontalProjection * Math.sin(groundAngle);
    }
}
