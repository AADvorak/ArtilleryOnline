package com.github.aadvorak.artilleryonline.battle.common.lines;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Boundaries;
import com.github.aadvorak.artilleryonline.battle.common.shapes.CircleShape;
import com.github.aadvorak.artilleryonline.battle.common.shapes.HalfCircleShape;
import com.github.aadvorak.artilleryonline.battle.common.shapes.Shape;
import com.github.aadvorak.artilleryonline.battle.common.shapes.TrapezeShape;

public interface BodyPart {

    BodyPosition position();

    Shape shape();

    Boundaries boundaries();

    static BodyPart of(BodyPosition position, Shape shape) {
        if (shape instanceof CircleShape circleShape) {
            return Circle.of(position, circleShape);
        }
        if (shape instanceof HalfCircleShape halfCircleShape) {
            return new HalfCircle(position, halfCircleShape);
        }
        if (shape instanceof TrapezeShape trapezeShape) {
            return new Trapeze(position, trapezeShape);
        }
        return null;
    }
}
