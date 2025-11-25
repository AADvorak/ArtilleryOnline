package com.github.aadvorak.artilleryonline.battle.calculator.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.calculator.utils.ForceCalculatorUtils;
import com.github.aadvorak.artilleryonline.battle.common.Constants;
import com.github.aadvorak.artilleryonline.battle.common.Force;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;

import java.util.ArrayList;
import java.util.List;

public class GroundReactionForceCalculator implements ForceCalculator<
        VehicleSpecs,
        VehiclePreCalc,
        VehicleConfig,
        VehicleState,
        VehicleModel,
        VehicleCalculations> {

    private static final String FORCE_DESCRIPTION = "Ground reaction";

    @Override
    public List<BodyForce> calculate(VehicleCalculations calculations, BattleModel battleModel) {
        var forces = new ArrayList<BodyForce>();
        if (calculations.getModel().getState().getVelocity().magnitude() < Constants.ZERO_THRESHOLD) {
            return forces;
        }
        var groundReactionCoefficient = battleModel.getRoom().getSpecs().getGroundReactionCoefficient();
        var groundMaxDepth = battleModel.getRoom().getSpecs().getGroundMaxDepth();
        addWheelReaction(forces, calculations.getRightWheel(), groundReactionCoefficient, groundMaxDepth);
        addWheelReaction(forces, calculations.getLeftWheel(), groundReactionCoefficient, groundMaxDepth);
        addTurretReaction(forces, calculations, groundReactionCoefficient, groundMaxDepth);
        return forces;
    }

    private void addWheelReaction(List<BodyForce> forces, WheelCalculations calculations,
                                  double groundReactionCoefficient, double groundMaxDepth) {
        if (calculations.getGroundContact() == null) {
            return;
        }
        var velocityNormalProjection = calculations.getVelocity()
                .projectionOnto(calculations.getGroundContact().normal());
        var velocityNormalProjectionMagnitude = velocityNormalProjection.magnitude();
        if (velocityNormalProjectionMagnitude > Constants.ZERO_THRESHOLD
                && velocityNormalProjection.dotProduct(calculations.getGroundContact().normal()) > 0) {
            var depth = Math.min(calculations.getGroundContact().depth(), groundMaxDepth);
            var force = Force.of(calculations.getGroundContact().normal()
                    .multiply(- velocityNormalProjectionMagnitude * depth * groundReactionCoefficient));
            forces.add(BodyForce.of(force, calculations.getGroundContact().position(),
                    calculations.getModel().getState().getPosition().getCenter(), FORCE_DESCRIPTION));
        }
    }

    private void addTurretReaction(List<BodyForce> forces, VehicleCalculations calculations,
                                   double groundReactionCoefficient, double groundMaxDepth) {
        if (calculations.getTurretGroundContacts() == null) {
            return;
        }
        calculations.getTurretGroundContacts().forEach(contact -> ForceCalculatorUtils.addReaction(forces,
                calculations, contact, groundMaxDepth, groundReactionCoefficient, FORCE_DESCRIPTION));
    }
}
