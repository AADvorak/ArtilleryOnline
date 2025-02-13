package com.github.aadvorak.artilleryonline.battle.common.lines;

import com.github.aadvorak.artilleryonline.battle.common.Position;

public record Segment(Position begin, Position end) {

    public Position findPointWithX(double targetX) {
        var x1 = begin().getX();
        var y1 = begin().getY();
        var x2 = end().getX();
        var y2 = end().getY();
        if ((targetX < x1 && targetX < x2) || (targetX > x1 && targetX > x2)) {
            return null;
        }
        var y = y1 + (targetX - x1) * (y2 - y1) / (x2 - x1);
        return new Position().setX(targetX).setY(y);
    }
}
