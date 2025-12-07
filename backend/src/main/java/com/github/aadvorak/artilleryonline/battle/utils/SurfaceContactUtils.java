package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.lines.*;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.state.SurfaceState;

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
        var maxDepth = withMaxDepth ? roomModel.getSpecs().getGroundMaxDepth() : 0.0;
        surfaces.forEach(surface -> getContact(bodyPart, surface, maxDepth).ifPresent(contacts::add));
        return contacts;
    }

    private static Optional<Contact> getContact(BodyPart bodyPart, SurfaceState surface, double maxDepth) {
        if (surface.getBoundaries().noOverlap(bodyPart.boundaries())) {
            return Optional.empty();
        }
        var contact = ContactUtils.getBodyPartsContact(bodyPart, surface.getTrapeze());
        if (contact != null) {
            if (maxDepth > 0) {
                return Optional.of(Contact.of(contact.depth() - maxDepth,
                        contact.normal(), contact.position()));
            }
            return Optional.of(contact);
        }
        return Optional.empty();
    }
}
