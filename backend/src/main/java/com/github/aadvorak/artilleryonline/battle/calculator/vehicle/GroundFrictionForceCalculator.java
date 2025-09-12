package com.github.aadvorak.artilleryonline.battle.calculator.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.common.Force;
import com.github.aadvorak.artilleryonline.battle.common.Vector;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;

import java.util.ArrayList;
import java.util.List;

public class GroundFrictionForceCalculator implements ForceCalculator<
        VehicleSpecs,
        VehiclePreCalc,
        VehicleConfig,
        VehicleState,
        VehicleModel,
        VehicleCalculations> {

    private static final String FORCE_DESCRIPTION = "Ground Friction";

    @Override
    public List<BodyForce> calculate(VehicleCalculations calculations, BattleModel battleModel) {
        var groundFrictionCoefficient = battleModel.getRoom().getSpecs().getGroundFrictionCoefficient();
        var gravityAcceleration = battleModel.getRoom().getSpecs().getGravityAcceleration();
        var contactsNumber = calculations.getGroundContacts().size();
        var forces = new ArrayList<BodyForce>();
        addWheelFriction(forces, calculations.getRightWheel(), groundFrictionCoefficient, gravityAcceleration, contactsNumber);
        addWheelFriction(forces, calculations.getLeftWheel(), groundFrictionCoefficient, gravityAcceleration, contactsNumber);
        addTurretFriction(forces, calculations, groundFrictionCoefficient, gravityAcceleration, contactsNumber);
        return forces;
    }

    private void addWheelFriction(List<BodyForce> forces, WheelCalculations wheelCalculations,
                                  double groundFrictionCoefficient, double gravityAcceleration,
                                  int contactsNumber) {
        var contact = wheelCalculations.getGroundContact();
        if (contact == null) {
            return;
        }
        var coefficient = groundFrictionCoefficient * gravityAcceleration * wheelCalculations.getMass()
                * Math.cos(contact.angle()) / contactsNumber / 10;
        var velocity = wheelCalculations.getVelocity().projectionOnto(
                Vector.tangential(contact.angle()));
        var force = new Force()
                .setX( - velocity.getX() * coefficient)
                .setY( - velocity.getY() * coefficient);
        forces.add(BodyForce.of(force, wheelCalculations.getPosition(), wheelCalculations.getModel().getState().getPosition().getCenter(),
                FORCE_DESCRIPTION + " Wheel"));
    }

    private void addTurretFriction(List<BodyForce> forces, VehicleCalculations calculations,
                                   double groundFrictionCoefficient, double gravityAcceleration,
                                   int contactsNumber) {
        if (calculations.getTurretGroundContacts() == null) {
            return;
        }
        var forceMultiplier = groundFrictionCoefficient * gravityAcceleration * calculations.getMass() / contactsNumber;
        calculations.getTurretGroundContacts().forEach(contact -> {
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
                forces.add(BodyForce.of(movingForce, contact.position(), calculations.getPosition(),
                        FORCE_DESCRIPTION + " Hull"));
            } else {
                var rotatingForce = new Force()
                        .setX(-rotatingVelocity.getX() * contactForceMultiplier)
                        .setY(-rotatingVelocity.getY() * contactForceMultiplier);
                forces.add(BodyForce.atCOM(rotatingForce, FORCE_DESCRIPTION + " Hull"));
            }
        });
    }
}
