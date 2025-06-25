package com.github.aadvorak.artilleryonline.battle.common.lines;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.shapes.TrapezeShape;

public record Trapeze(BodyPosition position, TrapezeShape shape) {

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

    private Position topCenter() {
        return position.getCenter().shifted(shape.getHeight(), position.getAngle() + Math.PI / 2);
    }
}
