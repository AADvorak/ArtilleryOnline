package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.lines.*;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SurfaceContactUtils {

    public static Set<Contact> getContacts(BodyPart bodyPart, RoomModel roomModel, boolean withMaxDepth) {
        var surfaces = roomModel.getState().getSurfaces();
        var contacts = new HashSet<Contact>();
        if (surfaces == null || surfaces.isEmpty()) {
            return contacts;
        }
        surfaces.stream()
                .map(surface -> new Segment(surface.getBegin(), surface.getEnd()))
                .forEach(segment -> getContact(bodyPart, segment, withMaxDepth).ifPresent(contacts::add));
        return contacts;
    }

    private static Optional<Contact> getContact(BodyPart bodyPart, Segment segment, boolean withMaxDepth) {
        if (
                bodyPart.minX() > segment.maxX()
                || bodyPart.maxX() < segment.minX()
                || bodyPart.minY() > segment.maxY()
                || bodyPart.maxY() < segment.minY()
        ) {
            return Optional.empty();
        }
        if (bodyPart instanceof Circle circle) {
            return getContact(circle, segment, withMaxDepth);
        }
        if (bodyPart instanceof HalfCircle halfCircle) {
            return getContact(halfCircle, segment, withMaxDepth);
        }
        if (bodyPart instanceof Trapeze trapeze) {
            return getContact(trapeze, segment, withMaxDepth);
        }
        return Optional.empty();
    }

    private static Optional<Contact> getContact(Circle circle, Segment segment, boolean withMaxDepth) {
        return Optional.empty();
    }

    private static Optional<Contact> getContact(HalfCircle halfCircle, Segment segment, boolean withMaxDepth) {
        return Optional.empty();
    }

    private static Optional<Contact> getContact(Trapeze trapeze, Segment segment, boolean withMaxDepth) {
        return Optional.empty();
    }
}
