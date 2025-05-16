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
        var forces = new ArrayList<BodyForce>();
        addWheelFriction(forces, calculations.getRightWheel(), groundFrictionCoefficient);
        addWheelFriction(forces, calculations.getLeftWheel(), groundFrictionCoefficient);
        addHullFriction(forces, calculations, groundFrictionCoefficient);
        return forces;
    }

    private void addWheelFriction(List<BodyForce> forces, WheelCalculations wheelCalculations,
                                  double groundFrictionCoefficient) {
        if (WheelGroundState.FULL_OVER_GROUND.equals(wheelCalculations.getGroundState())) {
            return;
        }
        var depth = wheelCalculations.getGroundContact().depth();
        var position = wheelCalculations.getGroundContact().position();
        var velocity = wheelCalculations.getVelocity().projectionOnto(
                Vector.tangential(wheelCalculations.getGroundContact().angle()));
        var force = new Force()
                .setX( - velocity.getX() * depth * groundFrictionCoefficient)
                .setY( - velocity.getY() * depth * groundFrictionCoefficient);
        forces.add(BodyForce.of(force, position, wheelCalculations.getModel().getState().getPosition().getCenter(),
                FORCE_DESCRIPTION + " Wheel"));
    }

    private void addHullFriction(List<BodyForce> forces, VehicleCalculations calculations,
                                 double groundFrictionCoefficient) {
        if (calculations.getGroundContacts() == null) {
            return;
        }
        calculations.getGroundContacts().forEach(contact -> {
            var tangential = Vector.tangential(contact.angle());
            var movingVelocity = calculations.getModel().getState().getVelocity().getMovingVelocity()
                    .projectionOnto(tangential);
            var rotatingVelocity = calculations.getModel().getState().getRotatingVelocityAt(contact.position())
                    .projectionOnto(tangential);
            var movingVelocityMagnitude = movingVelocity.magnitude();
            var rotatingVelocityMagnitude = rotatingVelocity.magnitude();
            if (movingVelocityMagnitude > rotatingVelocityMagnitude || rotatingVelocityMagnitude < 0.05) {
                var velocity = calculations.getModel().getState().getVelocityAt(contact.position());
                var movingForce = new Force()
                        .setX(-velocity.getX() * contact.depth() * groundFrictionCoefficient)
                        .setY(-velocity.getY() * contact.depth() * groundFrictionCoefficient);
                forces.add(BodyForce.of(movingForce, contact.position(), calculations.getPosition(),
                        FORCE_DESCRIPTION + " Hull"));
            } else {
                var rotatingForce = new Force()
                        .setX(-rotatingVelocity.getX() * contact.depth() * groundFrictionCoefficient)
                        .setY(-rotatingVelocity.getY() * contact.depth() * groundFrictionCoefficient);
                forces.add(BodyForce.atCOM(rotatingForce, FORCE_DESCRIPTION + " Hull"));
            }
        });
    }
}
