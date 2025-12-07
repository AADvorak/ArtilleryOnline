package com.github.aadvorak.artilleryonline.battle.common;

public record Boundaries(double xMin, double xMax, double yMin, double yMax) {

    public boolean noOverlap(Boundaries boundaries) {
        return xMax < boundaries.xMin || xMin > boundaries.xMax
                || yMax < boundaries.yMin || yMin > boundaries.yMax;
    }
}
