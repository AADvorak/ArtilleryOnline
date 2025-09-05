package com.github.aadvorak.artilleryonline.battle.calculator.box;

import com.github.aadvorak.artilleryonline.battle.calculations.BodyForce;
import com.github.aadvorak.artilleryonline.battle.calculations.BoxCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ForceCalculator;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Force;
import com.github.aadvorak.artilleryonline.battle.config.BoxConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.BoxModel;
import com.github.aadvorak.artilleryonline.battle.precalc.BoxPreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.BoxSpecs;
import com.github.aadvorak.artilleryonline.battle.state.BoxState;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GravityForceCalculator implements ForceCalculator<
        BoxSpecs,
        BoxPreCalc,
        BoxConfig,
        BoxState,
        BoxModel,
        BoxCalculations> {

    private static final String FORCE_DESCRIPTION = "Gravity";

    @Override
    public List<BodyForce> calculate(BoxCalculations calculations, BattleModel battleModel) {
        var roomGravityAcceleration = battleModel.getRoom().getSpecs().getGravityAcceleration();
        var mass = calculations.getMass();
        var forces = new ArrayList<BodyForce>();
        var groundContacts = calculations.getGroundContacts();
        var comX = calculations.getPosition().getX();
        var leftContacts = groundContacts.stream()
                .filter(contact -> contact.position().getX() < comX)
                .collect(Collectors.toSet());
        var rightContacts = groundContacts.stream()
                .filter(contact -> contact.position().getX() > comX)
                .collect(Collectors.toSet());
        if (leftContacts.isEmpty() || rightContacts.isEmpty()) {
            forces.add(BodyForce.atCOM(
                    new Force().setX(0.0).setY(-roomGravityAcceleration * mass),
                    FORCE_DESCRIPTION
            ));
        } else {
            var leftContact = getFarthest(leftContacts, comX);
            var rightContact = getFarthest(rightContacts, comX);
            addGroundForce(forces, leftContact, mass, roomGravityAcceleration);
            addGroundForce(forces, rightContact, mass, roomGravityAcceleration);
        }
        return forces;
    }

    private void addGroundForce(
            List<BodyForce> forces,
            Contact contact,
            double mass,
            double roomGravityAcceleration
    ) {
        final var maxFriction = 1.0;
        var forceModule = Math.max(
                Math.abs(roomGravityAcceleration * Math.sin(contact.angle())) * mass / 2 - maxFriction,
                0.0
        );
        if  (forceModule > 0.0) {
            forces.add(BodyForce.atCOM(
                    new Force()
                            .setX(-forceModule * Math.sin(contact.angle()))
                            .setY(-forceModule * Math.cos(contact.angle())),
                    FORCE_DESCRIPTION
            ));
        }
    }

    private Contact getFarthest(Set<Contact> contacts, double comX) {
        var iterator = contacts.iterator();
        var farthest = iterator.next();
        var farthestDistance = xDistance(farthest, comX);
        while (iterator.hasNext()) {
            var contact = iterator.next();
            var xDistance = xDistance(contact, comX);
            if (xDistance > farthestDistance) {
                farthest = contact;
                farthestDistance = xDistance;
            }
        }
        return farthest;
    }

    private double xDistance(Contact contact, double comX) {
        return Math.abs(contact.position().getX() - comX);
    }
}
