package com.github.aadvorak.artilleryonline.battle.calculator.utils;

import com.github.aadvorak.artilleryonline.battle.calculations.BodyCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.BodyForce;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Force;
import com.github.aadvorak.artilleryonline.battle.common.Vector;

import java.util.List;

public class ForceCalculatorUtils {

    public static void addFriction(
            List<BodyForce> forces, BodyCalculations<?,?,?,?,?> calculations,
            Contact contact, double forceMultiplier,
            String forceDescription) {
        var tangential = Vector.tangential(contact.angle());
        var movingVelocity = calculations.getModel().getState().getVelocity().getMovingVelocity()
                .projectionOnto(tangential);
        var rotatingVelocity = calculations.getModel().getState().getRotatingVelocityAt(contact.position())
                .projectionOnto(tangential);
        var movingVelocityMagnitude = movingVelocity.magnitude();
        var rotatingVelocityMagnitude = rotatingVelocity.magnitude();
        var contactForceMultiplier = forceMultiplier * Math.cos(contact.angle());
        if (movingVelocityMagnitude > rotatingVelocityMagnitude || rotatingVelocityMagnitude < 0.05) {
            var velocity = calculations.getModel().getState().getVelocityAt(contact.position());
            var movingForce = new Force()
                    .setX(-velocity.getX() * contactForceMultiplier)
                    .setY(-velocity.getY() * contactForceMultiplier);
            forces.add(BodyForce.of(movingForce, contact.position(), calculations.getPosition(), forceDescription));
        } else {
            var rotatingForce = new Force()
                    .setX(-rotatingVelocity.getX() * contactForceMultiplier)
                    .setY(-rotatingVelocity.getY() * contactForceMultiplier);
            forces.add(BodyForce.atCOM(rotatingForce, forceDescription));
        }
    }
}
