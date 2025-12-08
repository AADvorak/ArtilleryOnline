package com.github.aadvorak.artilleryonline;

import com.github.aadvorak.artilleryonline.battle.common.Constants;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
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
    private static final double EDGE_CIRCLE_DISTANCE = RADIUS - Constants.INTERPENETRATION_THRESHOLD
            + SURFACE_WIDTH / 2;

    private static final double SMALL_DELTA = 0.00001;

    private static final Position BODY_CENTER = new Position().setX(BODY_X).setY(BODY_Y);

    private static final Circle CIRCLE = new Circle(BODY_CENTER, RADIUS);

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
        var surfaceY = BODY_Y - EDGE_CIRCLE_DISTANCE + SMALL_DELTA + roomModel.getSpecs().getGroundMaxDepth();
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
        var surfaceY = BODY_Y - EDGE_CIRCLE_DISTANCE - SMALL_DELTA + roomModel.getSpecs().getGroundMaxDepth();
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
        var surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE - SMALL_DELTA - roomModel.getSpecs().getGroundMaxDepth();
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
        var surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE + SMALL_DELTA - roomModel.getSpecs().getGroundMaxDepth();
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
        var surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE - SMALL_DELTA - roomModel.getSpecs().getGroundMaxDepth();
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
        var surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE + SMALL_DELTA - roomModel.getSpecs().getGroundMaxDepth();
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
        var surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE + SMALL_DELTA + roomModel.getSpecs().getGroundMaxDepth();
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
        var surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE - SMALL_DELTA + roomModel.getSpecs().getGroundMaxDepth();
        var surface = new SurfaceState()
                .setBegin(new Position().setX(surfaceX).setY(BODY_Y - RADIUS))
                .setEnd(new Position().setX(surfaceX).setY(BODY_Y + RADIUS))
                .setWidth(SURFACE_WIDTH);
        roomModel.getState().setSurfaces(List.of(surface));
        var contacts = SurfaceContactUtils.getContacts(CIRCLE, roomModel, true);
        assertTrue(contacts.isEmpty());
    }
}
