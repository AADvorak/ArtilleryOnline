package com.github.aadvorak.artilleryonline.battle.common.lines;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.shapes.CircleShape;
import com.github.aadvorak.artilleryonline.battle.common.shapes.Shape;

public record Circle(Position center, double radius) implements BodyPart {

    @Override
    public BodyPosition position() {
        return BodyPosition.of(center, 0.0);
    }

    @Override
    public Shape shape() {
        return new CircleShape().setRadius(radius);
    }

    @Override
    public double maxX() {
        return center.getX() + radius;
    }

    @Override
    public double maxY() {
        return center.getY() + radius;
    }

    @Override
    public double minX() {
        return center.getX() - radius;
    }

    @Override
    public double minY() {
        return center.getY() - radius;
    }

    public static Circle of(BodyPosition position, CircleShape shape) {
        return new Circle(position.getCenter(), shape.getRadius());
    }
}
