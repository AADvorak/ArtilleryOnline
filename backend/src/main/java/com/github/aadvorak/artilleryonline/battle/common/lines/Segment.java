package com.github.aadvorak.artilleryonline.battle.common.lines;

import com.github.aadvorak.artilleryonline.battle.common.Constants;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Vector;

public record Segment(Position begin, Position end) {

    public Position findPointWithX(double targetX) {
        var x1 = begin().getX();
        var y1 = begin().getY();
        var x2 = end().getX();
        var y2 = end().getY();
        var position = new Position().setX(targetX);
        if  (x1 == x2) {
            return Math.abs(targetX - x1) < Constants.ZERO_THRESHOLD
                    ? position.setY(y1 + y2 / 2) : null;
        }
        if ((targetX < x1 && targetX < x2) || (targetX > x1 && targetX > x2)) {
            return null;
        }
        var y = y1 + (targetX - x1) * (y2 - y1) / (x2 - x1);
        return position.setY(y);
    }

    public Vector normal() {
        return Vector.normal(end.angleTo(begin));
    }

    public Position center() {
        return new Position()
                .setX((begin.getX() + end.getX()) / 2)
                .setY((begin.getY() + end.getY()) / 2);
    }
}
