package com.github.aadvorak.artilleryonline.battle.calculator.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Force;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GravityForceCalculator implements ForceCalculator<
        VehicleSpecs,
        VehiclePreCalc,
        VehicleConfig,
        VehicleState,
        VehicleModel,
        VehicleCalculations> {

    private static final String FORCE_DESCRIPTION = "Gravity";

    @Override
    public List<BodyForce> calculate(VehicleCalculations calculations, BattleModel battleModel) {
        var roomGravityAcceleration = battleModel.getRoom().getSpecs().getGravityAcceleration();
        var mass = calculations.getMass();
        var forces = new ArrayList<BodyForce>();
        var groundContacts = new HashSet<>(calculations.getGroundContacts());
        if (calculations.getRightWheel().getGroundContact() != null) {
            groundContacts.add(calculations.getRightWheel().getGroundContact());
        }
        if (calculations.getLeftWheel().getGroundContact() != null) {
            groundContacts.add(calculations.getLeftWheel().getGroundContact());
        }
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
            var groundGravityDepth = 0.7 * battleModel.getRoom().getSpecs().getGroundMaxDepth();
            if (leftContact.depth() <= groundGravityDepth && rightContact.depth() <= groundGravityDepth) {
                addGroundForce(forces, leftContact, calculations.getPosition(), mass, roomGravityAcceleration, groundGravityDepth);
                addGroundForce(forces, rightContact, calculations.getPosition(), mass, roomGravityAcceleration, groundGravityDepth);
            }
        }
        return forces;
    }

    private void addGroundForce(
            List<BodyForce> forces,
            Contact contact,
            Position comPosition,
            double mass,
            double roomGravityAcceleration,
            double groundGravityDepth
    ) {
        var groundAccelerationModule = Math.abs(roomGravityAcceleration * Math.sin(contact.angle()))
                * Math.sqrt(1 - contact.depth() / groundGravityDepth) * mass / 2;
        forces.add(BodyForce.of(
                new Force()
                        .setX(-groundAccelerationModule * Math.sin(contact.angle()))
                        .setY(-groundAccelerationModule * Math.cos(contact.angle())),
                contact.position(),
                comPosition,
                FORCE_DESCRIPTION
        ));
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
