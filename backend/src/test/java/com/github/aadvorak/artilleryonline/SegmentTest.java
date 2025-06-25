package com.github.aadvorak.artilleryonline;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SegmentTest {

    private static final double EPSILON = 0.00001;

    private static final Position position00 = new Position();

    private static final Position position01 = new Position().setY(1.0);

    private static final Position position10 = new Position().setX(1.0);

    @Test
    public void horizontalLeftToRight() {
        var segment = new Segment(position00, position10);
        var normal = segment.normal();
        assertAll(
                () -> assertEquals(0.0, normal.getX(), EPSILON),
                () -> assertEquals(1.0, normal.getY(), EPSILON)
        );
    }

    @Test
    public void horizontalRightToLeft() {
        var segment = new Segment(position10, position00);
        var normal = segment.normal();
        assertAll(
                () -> assertEquals(0.0, normal.getX(), EPSILON),
                () -> assertEquals(-1.0, normal.getY(), EPSILON)
        );
    }

    @Test
    public void verticalBottomToTop() {
        var segment = new Segment(position00, position01);
        var normal = segment.normal();
        assertAll(
                () -> assertEquals(-1.0, normal.getX(), EPSILON),
                () -> assertEquals(0.0, normal.getY(), EPSILON)
        );
    }

    @Test
    public void verticalTopToBottom() {
        var segment = new Segment(position01, position00);
        var normal = segment.normal();
        assertAll(
                () -> assertEquals(1.0, normal.getX(), EPSILON),
                () -> assertEquals(0.0, normal.getY(), EPSILON)
        );
    }
}
