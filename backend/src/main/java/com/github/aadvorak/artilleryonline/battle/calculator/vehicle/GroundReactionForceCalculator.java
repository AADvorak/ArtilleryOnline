package com.github.aadvorak.artilleryonline.battle.calculator.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.common.VectorProjections;
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
        var groundReactionCoefficient = battleModel.getRoom().getSpecs().getGroundReactionCoefficient();
        var forces = new ArrayList<BodyForce>();
        addWheelFriction(forces, calculations.getRightWheel(), groundReactionCoefficient);
        addWheelFriction(forces, calculations.getLeftWheel(), groundReactionCoefficient);
        return forces;
    }

    private void addWheelFriction(List<BodyForce> forces, WheelCalculations calculations,
                                  double groundReactionCoefficient) {
        if (WheelGroundState.FULL_OVER_GROUND.equals(calculations.getGroundState())
                || WheelGroundState.FULL_UNDER_GROUND.equals(calculations.getGroundState())) {
            return;
        }
        var groundAngle = calculations.getGroundContact().angle();
        var velocityNormalProjection = calculations.getVelocity().projections(groundAngle).getNormal();
        if (velocityNormalProjection < 0) {
            var forceProjections = new VectorProjections(groundAngle)
                    .setNormal(- velocityNormalProjection * calculations.getGroundContact().depth()
                            * groundReactionCoefficient);
            var force = forceProjections.recoverForce();
            forces.add(BodyForce.of(force, calculations.getGroundContact().position(), FORCE_DESCRIPTION));
        }
    }
}
