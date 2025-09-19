package com.github.aadvorak.artilleryonline.battle.calculator.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.calculator.utils.ForceCalculatorUtils;
import com.github.aadvorak.artilleryonline.battle.common.Constants;
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
        var forces = new ArrayList<BodyForce>();
        if (calculations.getModel().getState().getVelocity().magnitude() < Constants.ZERO_THRESHOLD) {
            return forces;
        }
        var groundFrictionCoefficient = battleModel.getRoom().getSpecs().getGroundFrictionCoefficient();
        var gravityAcceleration = battleModel.getRoom().getSpecs().getGravityAcceleration();
        var contactsNumber = calculations.getGroundContacts().size();
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
        calculations.getTurretGroundContacts().forEach(contact -> ForceCalculatorUtils.addFriction(forces,
                calculations, contact, forceMultiplier, FORCE_DESCRIPTION + " Hull"));
    }
}
