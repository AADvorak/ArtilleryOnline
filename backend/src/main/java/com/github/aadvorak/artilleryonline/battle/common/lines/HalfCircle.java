package com.github.aadvorak.artilleryonline.battle.common.lines;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Boundaries;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.shapes.HalfCircleShape;

public record HalfCircle(BodyPosition position, HalfCircleShape shape) implements BodyPart {

    public Position bottomRight() {
        return position.getCenter().shifted(shape.getRadius(), position.getAngle());
    }

    public Position bottomLeft() {
        return position.getCenter().shifted(-shape.getRadius(), position.getAngle());
    }

    public Position center() {
        return position.getCenter();
    }

    public double radius() {
        return shape.getRadius();
    }

    public double angle() {
        return position.getAngle();
    }

    public Segment chord() {
        return new Segment(bottomLeft(), bottomRight());
    }

    public Circle circle() {
        return new Circle(position().getCenter(), shape.getRadius());
    }

    public static HalfCircle of(BodyPosition bodyPosition, double radius) {
        return new HalfCircle(bodyPosition, new HalfCircleShape().setRadius(radius));
    }

    public static HalfCircle of(Position position, double angle, double radius) {
        return new HalfCircle(BodyPosition.of(position, angle), new HalfCircleShape().setRadius(radius));
    }

    @Override
    public Boundaries boundaries() {
        return new Boundaries(
                position.getX() - shape.getRadius(),
                position.getX() + shape.getRadius(),
                position.getY() - shape.getRadius(),
                position.getY() + shape.getRadius()
        );
    }
}
