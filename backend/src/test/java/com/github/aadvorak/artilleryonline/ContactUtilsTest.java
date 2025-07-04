package com.github.aadvorak.artilleryonline;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.HalfCircle;
import com.github.aadvorak.artilleryonline.battle.common.lines.Trapeze;
import com.github.aadvorak.artilleryonline.battle.common.shapes.TrapezeShape;
import com.github.aadvorak.artilleryonline.battle.utils.ContactUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContactUtilsTest {

    private static final double SMALL_DELTA = 0.00001;

    private static final double SQRT_05 = Math.sqrt(0.5);

    private static final TrapezeShape RECT_TRAPEZE_SHAPE = new TrapezeShape()
            .setBottomRadius(1.0)
            .setTopRadius(1.0)
            .setHeight(1.0);

    private static final TrapezeShape SMALLER_TOP_TRAPEZE_SHAPE = new TrapezeShape()
            .setBottomRadius(1.0)
            .setTopRadius(0.4)
            .setHeight(1.0);

    @Test
    public void trapezesBottomWithBottomLeft() {
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setX(-0.5)
                        .setY(-0.5)
                        .setAngle(-3 * Math.PI / 4),
                RECT_TRAPEZE_SHAPE
        );
        var otherTrapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(1.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezesRightWithTopLeft() {
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setX(2.2)
                        .setAngle(Math.PI / 8),
                RECT_TRAPEZE_SHAPE
        );
        var otherTrapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(-1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezesTopWithBottomRight() {
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setY(1.2)
                        .setAngle(-Math.PI / 8),
                RECT_TRAPEZE_SHAPE
        );
        var otherTrapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(-1.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezesLeftWithTopRight() {
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setX(-2.2)
                        .setAngle(-Math.PI / 8),
                RECT_TRAPEZE_SHAPE
        );
        var otherTrapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezesBottomWithTop() {
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setY(-0.9),
                SMALLER_TOP_TRAPEZE_SHAPE
        );
        var otherTrapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(1.0, contact.normal().getY(), SMALL_DELTA),
                () -> assertEquals(0.1, contact.depth(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezesRightWithTop() {
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setX(1.9)
                        .setY(0.5)
                        .setAngle(Math.PI / 2),
                SMALLER_TOP_TRAPEZE_SHAPE
        );
        var otherTrapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(-1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA),
                () -> assertEquals(0.1, contact.depth(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezesTopRightWithBottomLeft() {
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setX(-1.9)
                        .setY(-0.9),
                RECT_TRAPEZE_SHAPE
        );
        var otherTrapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(SQRT_05, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(SQRT_05, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezesTopLeftWithBottomRight() {
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setX(1.9)
                        .setY(-0.9),
                RECT_TRAPEZE_SHAPE
        );
        var otherTrapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(-SQRT_05, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(SQRT_05, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezesBottomLeftWithTopRight() {
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setX(1.9)
                        .setY(0.9),
                RECT_TRAPEZE_SHAPE
        );
        var otherTrapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(-SQRT_05, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(-SQRT_05, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezesBottomRightWithTopLeft() {
        var trapeze = new Trapeze(
                new BodyPosition()
                        .setX(-1.9)
                        .setY(0.9),
                RECT_TRAPEZE_SHAPE
        );
        var otherTrapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(SQRT_05, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(-SQRT_05, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeBottomRightWithCircle() {
        var trapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var circle = new Circle(new Position().setX(1.5).setY(-0.5), 1.0);
        var contact = ContactUtils.getTrapezeCircleContact(trapeze, circle);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(SQRT_05, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(-SQRT_05, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeBottomWithCircle() {
        var trapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var circle = new Circle(new Position().setY(-0.5), 1.0);
        var contact = ContactUtils.getTrapezeCircleContact(trapeze, circle);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(-1.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeBottomWithCircleCenterInTrapeze() {
        var trapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var circle = new Circle(new Position().setY(0.1), 0.2);
        var contact = ContactUtils.getTrapezeCircleContact(trapeze, circle);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(-1.0, contact.normal().getY(), SMALL_DELTA),
                () -> assertEquals(0.3, contact.depth(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeBottomWithCircleFullInTrapeze() {
        var trapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var circle = new Circle(new Position().setY(0.11), 0.1);
        var contact = ContactUtils.getTrapezeCircleContact(trapeze, circle);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(-1.0, contact.normal().getY(), SMALL_DELTA),
                () -> assertEquals(0.21, contact.depth(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeWithCircleNoContact() {
        var trapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var circle = new Circle(new Position().setY(-0.11), 0.1);
        var contact = ContactUtils.getTrapezeCircleContact(trapeze, circle);
        assertNull(contact);
    }

    @Test
    public void trapezeWithHalfCircleNoContact() {
        var trapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var halfCircle = HalfCircle.of(new BodyPosition().setY(RECT_TRAPEZE_SHAPE.getHeight()), 1.0);
        var contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle);
        assertNull(contact);
    }

    @Test
    public void trapezeTopWithHalfCircleBottom() {
        var depth = 0.1;
        var trapeze = new Trapeze(
                new BodyPosition().setY(depth),
                SMALLER_TOP_TRAPEZE_SHAPE
        );
        var halfCircle = HalfCircle.of(new BodyPosition().setY(RECT_TRAPEZE_SHAPE.getHeight()), 1.0);
        var contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(1.0, contact.normal().getY(), SMALL_DELTA),
                () -> assertEquals(depth, contact.depth(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeTopWithHalfCircleTop() {
        var depth = 0.1;
        var trapeze = new Trapeze(
                new BodyPosition().setY(depth),
                SMALLER_TOP_TRAPEZE_SHAPE
        );
        var halfCircle = HalfCircle.of(new BodyPosition()
                .setY(RECT_TRAPEZE_SHAPE.getHeight() + 1.0)
                .setAngle(Math.PI), 1.0);
        var contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(1.0, contact.normal().getY(), SMALL_DELTA),
                () -> assertEquals(depth, contact.depth(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeBottomWithHalfCircleTop() {
        var depth = 0.1;
        var trapeze = new Trapeze(
                new BodyPosition().setY(-depth),
                SMALLER_TOP_TRAPEZE_SHAPE
        );
        var halfCircle = HalfCircle.of(new BodyPosition().setY(-1.0), 1.0);
        var contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(0.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(-1.0, contact.normal().getY(), SMALL_DELTA),
                () -> assertEquals(depth, contact.depth(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeBottomRightWithHalfCircleTop() {
        var trapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var halfCircle = HalfCircle.of(new BodyPosition()
                .setX(1.5)
                .setY(-0.5)
                .setAngle(Math.PI / 4), 1.0);
        var contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(SQRT_05, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(-SQRT_05, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeTopRightWithHalfCircleBottom() {
        var trapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var halfCircle = HalfCircle.of(new BodyPosition()
                .setX(0.9)
                .setY(0.9)
                .setAngle(-Math.PI / 4), 1.0);
        var contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(SQRT_05, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(SQRT_05, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeRightWithHalfCircleLeftBottom() {
        var trapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var halfCircle = HalfCircle.of(new BodyPosition()
                .setX(1.9)
                .setY(0.5), 1.0);
        var contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(1.0, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(0.0, contact.normal().getY(), SMALL_DELTA)
        );
    }

    @Test
    public void trapezeTopRightWithHalfCircleLeftBottom() {
        var trapeze = new Trapeze(
                new BodyPosition(),
                RECT_TRAPEZE_SHAPE
        );
        var halfCircle = HalfCircle.of(new BodyPosition()
                .setX(1.9)
                .setY(0.9), 1.0);
        var contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle);
        assertNotNull(contact);
        assertAll(
                () -> assertEquals(SQRT_05, contact.normal().getX(), SMALL_DELTA),
                () -> assertEquals(SQRT_05, contact.normal().getY(), SMALL_DELTA)
        );
    }
}
