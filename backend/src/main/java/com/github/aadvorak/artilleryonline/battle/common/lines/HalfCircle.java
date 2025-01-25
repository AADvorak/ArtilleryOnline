package com.github.aadvorak.artilleryonline.battle.common.lines;

import com.github.aadvorak.artilleryonline.battle.common.Position;

public record HalfCircle(Position center, double radius, double angle) {

    public Segment chord() {
        return new Segment(
                new Position()
                        .setX(center.getX() + radius * Math.cos(angle))
                        .setY(center.getY() + radius * Math.sin(angle)),
                new Position()
                        .setX(center.getX() - radius * Math.cos(angle))
                        .setY(center.getY() - radius * Math.sin(angle))
        );
    }

    public Circle circle() {
        return new Circle(center, radius);
    }
}
