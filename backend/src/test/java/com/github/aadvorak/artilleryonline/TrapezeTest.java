package com.github.aadvorak.artilleryonline;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.lines.Trapeze;
import com.github.aadvorak.artilleryonline.battle.common.shapes.TrapezeShape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TrapezeTest {

    private static final double EPSILON = 0.00001;

    private static final TrapezeShape SHAPE = new TrapezeShape()
            .setBottomRadius(2.0)
            .setTopRadius(1.0)
            .setHeight(1.0);

    private static final Trapeze TRAPEZE = new Trapeze(new BodyPosition(), SHAPE);

    @Test
    public void horizontalUp() {
        var bottomRight = TRAPEZE.bottomRight();
        var bottomLeft = TRAPEZE.bottomLeft();
        var topLeft = TRAPEZE.topLeft();
        var topRight = TRAPEZE.topRight();
        assertAll(
                () -> assertEquals(2.0, bottomRight.getX(), EPSILON),
                () -> assertEquals(0.0, bottomRight.getY(), EPSILON),
                () -> assertEquals(-2.0, bottomLeft.getX(), EPSILON),
                () -> assertEquals(0.0, bottomLeft.getY(), EPSILON),
                () -> assertEquals(-1.0, topLeft.getX(), EPSILON),
                () -> assertEquals(1.0, topLeft.getY(), EPSILON),
                () -> assertEquals(1.0, topRight.getX(), EPSILON),
                () -> assertEquals(1.0, topRight.getY(), EPSILON)
        );
    }

    // todo more tests
}
