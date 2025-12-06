package com.github.aadvorak.artilleryonline.battle.common.lines;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.shapes.TrapezeShape;

public record Trapeze(BodyPosition position, TrapezeShape shape) implements BodyPart {

    public Position bottomRight() {
        return position.getCenter().shifted(shape.getBottomRadius(), position.getAngle());
    }

    public Position bottomLeft() {
        return position.getCenter().shifted(-shape.getBottomRadius(), position.getAngle());
    }

    public Position topRight() {
        return topCenter().shifted(shape.getTopRadius(), position.getAngle());
    }

    public Position topLeft() {
        return topCenter().shifted(-shape.getTopRadius(), position.getAngle());
    }

    public Segment bottom() {
        return new Segment(bottomLeft(), bottomRight());
    }

    public Segment top() {
        return new Segment(topRight(), topLeft());
    }

    public Segment right() {
        return new Segment(bottomRight(), topRight());
    }

    public Segment left() {
        return new Segment(topLeft(), bottomLeft());
    }

    public double maxDistanceFromCenter() {
        var topCornerDistance = Math.sqrt(shape.getTopRadius() * shape.getTopRadius()
                + shape.getHeight() * shape.getHeight());
        return Math.max(topCornerDistance, shape.getBottomRadius());
    }

    private Position topCenter() {
        return position.getCenter().shifted(shape.getHeight(), position.getAngle() + Math.PI / 2);
    }

    @Override
    public double maxX() {
        return position.getX() + shape.getMaxSize();
    }

    @Override
    public double maxY() {
        return position.getY() + shape.getMaxSize();
    }

    @Override
    public double minX() {
        return position.getX() - shape.getMaxSize();
    }

    @Override
    public double minY() {
        return position.getY() - shape.getMaxSize();
    }
}
