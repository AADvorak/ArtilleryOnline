package com.github.aadvorak.artilleryonline.battle.common.lines;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.shapes.Shape;

public interface BodyPart {

    BodyPosition position();

    Shape shape();
}
