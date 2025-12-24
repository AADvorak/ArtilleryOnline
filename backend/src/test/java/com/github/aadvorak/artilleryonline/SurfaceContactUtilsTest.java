package com.github.aadvorak.artilleryonline;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Constants;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.HalfCircle;
import com.github.aadvorak.artilleryonline.battle.common.lines.Trapeze;
import com.github.aadvorak.artilleryonline.battle.common.shapes.CircleShape;
import com.github.aadvorak.artilleryonline.battle.common.shapes.TrapezeShape;
import com.github.aadvorak.artilleryonline.battle.state.SurfaceState;
import com.github.aadvorak.artilleryonline.battle.utils.SurfaceContactUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SurfaceContactUtilsTest {

    private static final double BODY_X = 10.0;
    private static final double BODY_Y = 10.0;
    private static final double RADIUS = 1.0;
    private static final double SURFACE_WIDTH = 0.1;
    private static final double EDGE_DISTANCE = SURFACE_WIDTH / 2 - Constants.INTERPENETRATION_THRESHOLD;
    private static final double EDGE_CIRCLE_DISTANCE = RADIUS + EDGE_DISTANCE;

    private static final double SMALL_DELTA = 0.00001;

    private static final BodyPosition BODY_POSITION = new BodyPosition().setX(BODY_X).setY(BODY_Y);

    private static final Circle CIRCLE = Circle.of(BODY_POSITION, new CircleShape().setRadius(RADIUS));

    private static final HalfCircle HALF_CIRCLE = HalfCircle.of(BODY_POSITION, RADIUS);

    private static final Trapeze TRAPEZE = new Trapeze(BODY_POSITION, TrapezeShape.rectangle(2 * RADIUS, RADIUS));

    @Test
    public void circleBottomContactExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y - EDGE_CIRCLE_DISTANCE + SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, false);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(-1.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void circleBottomContactNotExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y - EDGE_CIRCLE_DISTANCE - SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, false);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void circleBottomContactExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y - EDGE_CIRCLE_DISTANCE + SMALL_DELTA + roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, true);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(-1.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void circleBottomContactNotExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y - EDGE_CIRCLE_DISTANCE - SMALL_DELTA + roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, true);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void circleTopContactExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE - SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, false);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(1.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void circleTopContactNotExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE + SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, false);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void circleTopContactExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE - SMALL_DELTA - roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, true);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(1.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void circleTopContactNotExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE + SMALL_DELTA - roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, true);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void circleRightContactExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE - SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, false);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void circleRightContactNotExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE + SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, false);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void circleRightContactExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE - SMALL_DELTA - roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, true);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void circleRightContactNotExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE + SMALL_DELTA - roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, true);
        assertTrue(contacts.isEmpty());
    }

@Test
    public void circleLeftContactExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE + SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, false);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(-1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void circleLeftContactNotExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE - SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, false);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void circleLeftContactExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE + SMALL_DELTA + roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, true);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(-1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void circleLeftContactNotExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE - SMALL_DELTA + roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, true);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void halfCircleBottomContactExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y - EDGE_DISTANCE + SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, false);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(-1.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    // todo @Test
    public void halfCircleBottomContactNotExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y - EDGE_DISTANCE - SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, false);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void halfCircleBottomContactExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y - EDGE_DISTANCE + SMALL_DELTA + roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, true);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(-1.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    // todo @Test
    public void halfCircleBottomContactNotExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y - EDGE_DISTANCE - SMALL_DELTA + roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, true);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void halfCircleTopContactExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE - SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, false);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(1.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void halfCircleTopContactNotExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE + SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, false);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void halfCircleTopContactExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE - SMALL_DELTA - roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, true);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(1.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void halfCircleTopContactNotExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE + SMALL_DELTA - roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, true);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void halfCircleRightContactExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE - SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, false);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void halfCircleRightContactNotExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE + SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, false);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void halfCircleRightContactExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE - SMALL_DELTA - roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, true);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void halfCircleRightContactNotExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE + SMALL_DELTA - roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, true);
        assertTrue(contacts.isEmpty());
    }

@Test
    public void halfCircleLeftContactExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE + SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, false);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(-1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void halfCircleLeftContactNotExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE - SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, false);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void halfCircleLeftContactExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE + SMALL_DELTA + roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, true);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(-1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void halfCircleLeftContactNotExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE - SMALL_DELTA + roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, roomModel, true);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void trapezeBottomContactExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y - EDGE_DISTANCE + SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - 2 * RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + 2 * RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, false);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(-1.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeBottomContactNotExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y - EDGE_DISTANCE - SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - 2 * RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + 2 * RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, false);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void trapezeBottomContactExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y - EDGE_DISTANCE + SMALL_DELTA + roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - 2 * RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + 2 * RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, true);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(-1.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeBottomContactNotExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y - EDGE_DISTANCE - SMALL_DELTA + roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - 2 * RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + 2 * RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, true);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void trapezeTopContactExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE - SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - 2 * RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + 2 * RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, false);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(1.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeTopContactNotExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE + SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - 2 * RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + 2 * RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, false);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void trapezeTopContactExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE - SMALL_DELTA - roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - 2 * RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + 2 * RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, true);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(1.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeTopContactNotExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE + SMALL_DELTA - roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(BODY_X - 2 * RADIUS).setY(surfaceY))
                .setEnd(new Position().setX(BODY_X + 2 * RADIUS).setY(surfaceY))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, true);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void trapezeRightContactExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE - SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - 2 * RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + 2 * RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, false);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeRightContactNotExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE + SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - 2 * RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + 2 * RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, false);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void trapezeRightContactExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE - SMALL_DELTA - roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - 2 * RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + 2 * RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, true);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeRightContactNotExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE + SMALL_DELTA - roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - 2 * RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + 2 * RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, true);
        assertTrue(contacts.isEmpty());
    }

@Test
    public void trapezeLeftContactExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE + SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - 2 * RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + 2 * RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, false);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(-1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeLeftContactNotExists() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE - SMALL_DELTA;
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - 2 * RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + 2 * RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, false);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void trapezeLeftContactExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE + SMALL_DELTA + roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - 2 * RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + 2 * RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, true);
        assertEquals(1, contacts.size());
        var contact = contacts.stream().findAny().get();
        assertAll(
                () -> assertEquals(-1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeLeftContactNotExistsWithMaxDepth() {
        var roomModel = TestRoomGenerator.generate();
        var surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE - SMALL_DELTA + roomModel.getSpecs().getSurfaceMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - 2 * RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + 2 * RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(TRAPEZE, roomModel, true);
        assertTrue(contacts.isEmpty());
    }
}
