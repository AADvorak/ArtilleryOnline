package com.github.aadvorak.artilleryonline;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Constants;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.Trapeze;
import com.github.aadvorak.artilleryonline.battle.common.shapes.TrapezeShape;
import com.github.aadvorak.artilleryonline.battle.utils.GroundContactUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GroundContactUtilsTest {

    private static final double MIDDLE_ROOM_X = 10.0;
    private static final double RADIUS = 0.5;
    private static final double EDGE_HEIGHT = TestRoomGenerator.GROUND_LEVEL - Constants.INTERPENETRATION_THRESHOLD;
    private static final double SMALL_DELTA = 0.00001;

    private static final TrapezeShape TRAPEZE_SHAPE = new TrapezeShape()
            .setBottomRadius(1.0)
            .setTopRadius(1.0)
            .setHeight(1.0);

    @Test
    public void circleGroundContactExists() {
        var roomModel = TestRoomGenerator.generate();
        var circle = new Circle(new Position().setX(MIDDLE_ROOM_X).setY(EDGE_HEIGHT + RADIUS - SMALL_DELTA), RADIUS);
        var contact = GroundContactUtils.getGroundContact(circle, roomModel, false);
        assertNotNull(contact);
    }

    @Test
    public void circleGroundContactNotExists() {
        var roomModel = TestRoomGenerator.generate();
        var circle = new Circle(new Position().setX(MIDDLE_ROOM_X).setY(EDGE_HEIGHT + RADIUS + SMALL_DELTA), RADIUS);
        var contact = GroundContactUtils.getGroundContact(circle, roomModel, false);
        assertNull(contact);
    }

    @Test
    public void trapezeBottomGroundContactsExist() {
        var roomModel = TestRoomGenerator.generate();
        var trapeze = new Trapeze(
                new BodyPosition().setX(MIDDLE_ROOM_X).setY(EDGE_HEIGHT - SMALL_DELTA),
                TRAPEZE_SHAPE
        );
        var contacts = GroundContactUtils.getGroundContacts(trapeze, roomModel, false);
        assertFalse(contacts.isEmpty());
    }

    @Test
    public void trapezeBottomGroundContactsNotExist() {
        var roomModel = TestRoomGenerator.generate();
        var trapeze = new Trapeze(
                new BodyPosition().setX(MIDDLE_ROOM_X).setY(EDGE_HEIGHT + SMALL_DELTA),
                TRAPEZE_SHAPE
        );
        var contacts = GroundContactUtils.getGroundContacts(trapeze, roomModel, false);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void trapezeTopGroundContactsExist() {
        var roomModel = TestRoomGenerator.generate();
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setX(MIDDLE_ROOM_X)
                        .setY(EDGE_HEIGHT - SMALL_DELTA + TRAPEZE_SHAPE.getHeight())
                        .setAngle(Math.PI),
                TRAPEZE_SHAPE
        );
        var contacts = GroundContactUtils.getGroundContacts(trapeze, roomModel, false);
        assertFalse(contacts.isEmpty());
    }

    @Test
    public void trapezeTopGroundContactsNotExist() {
        var roomModel = TestRoomGenerator.generate();
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setX(MIDDLE_ROOM_X)
                        .setY(EDGE_HEIGHT + SMALL_DELTA + TRAPEZE_SHAPE.getHeight())
                        .setAngle(Math.PI),
                TRAPEZE_SHAPE
        );
        var contacts = GroundContactUtils.getGroundContacts(trapeze, roomModel, false);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void trapezeRightGroundContactsExist() {
        var roomModel = TestRoomGenerator.generate();
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setX(MIDDLE_ROOM_X)
                        .setY(EDGE_HEIGHT - SMALL_DELTA + TRAPEZE_SHAPE.getBottomRadius())
                        .setAngle(-Math.PI / 2),
                TRAPEZE_SHAPE
        );
        var contacts = GroundContactUtils.getGroundContacts(trapeze, roomModel, false);
        assertFalse(contacts.isEmpty());
    }

    @Test
    public void trapezeRightGroundContactsNotExist() {
        var roomModel = TestRoomGenerator.generate();
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setX(MIDDLE_ROOM_X)
                        .setY(EDGE_HEIGHT + SMALL_DELTA + TRAPEZE_SHAPE.getBottomRadius())
                        .setAngle(-Math.PI / 2),
                TRAPEZE_SHAPE
        );
        var contacts = GroundContactUtils.getGroundContacts(trapeze, roomModel, false);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void trapezeLeftGroundContactsExist() {
        var roomModel = TestRoomGenerator.generate();
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setX(MIDDLE_ROOM_X)
                        .setY(EDGE_HEIGHT - SMALL_DELTA + TRAPEZE_SHAPE.getBottomRadius())
                        .setAngle(Math.PI / 2),
                TRAPEZE_SHAPE
        );
        var contacts = GroundContactUtils.getGroundContacts(trapeze, roomModel, false);
        assertFalse(contacts.isEmpty());
    }

    @Test
    public void trapezeLeftGroundContactsNotExist() {
        var roomModel = TestRoomGenerator.generate();
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setX(MIDDLE_ROOM_X)
                        .setY(EDGE_HEIGHT + SMALL_DELTA + TRAPEZE_SHAPE.getBottomRadius())
                        .setAngle(Math.PI / 2),
                TRAPEZE_SHAPE
        );
        var contacts = GroundContactUtils.getGroundContacts(trapeze, roomModel, false);
        assertTrue(contacts.isEmpty());
    }
}
