package com.github.aadvorak.artilleryonline.battle.calculator.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Force;
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
    public List<ForceAtPoint> calculate(VehicleCalculations calculations, BattleModel battleModel) {
        var roomGravityAcceleration = battleModel.getRoom().getSpecs().getGravityAcceleration();
        var mass = calculations.getMass();
        var forces = new ArrayList<ForceAtPoint>();
        if (WheelGroundState.FULL_OVER_GROUND.equals(calculations.getRightWheel().getGroundState())
                || WheelGroundState.FULL_OVER_GROUND.equals(calculations.getLeftWheel().getGroundState())) {
            forces.add(ForceAtPoint.atCOM(
                    new Force().setX(0.0).setY(-roomGravityAcceleration * mass),
                    FORCE_DESCRIPTION
            ));
        } else {
            var leftContact = getFarthest(leftContacts, comX);
            var rightContact = getFarthest(rightContacts, comX);
            var groundGravityDepth = 0.7 * battleModel.getRoom().getSpecs().getGroundMaxDepth();
            addWheelHalfOverGroundAcceleration(forces, calculations.getRightWheel(), mass,
                    roomGravityAcceleration, groundGravityDepth);
            addWheelHalfOverGroundAcceleration(forces, calculations.getLeftWheel(), mass,
                    roomGravityAcceleration, groundGravityDepth);
        }
        return forces;
    }

    private void addWheelHalfOverGroundAcceleration(
            List<ForceAtPoint> forces,
            Contact contact,
            double mass,
            double roomGravityAcceleration,
            double groundGravityDepth
    ) {
        if (WheelGroundState.HALF_OVER_GROUND.equals(calculations.getGroundState())
                && calculations.getGroundContact().depth() <= groundGravityDepth) {
            var groundAngle = calculations.getGroundContact().angle();
            var groundAccelerationModule = Math.abs(roomGravityAcceleration * Math.sin(groundAngle))
                    * Math.sqrt(1 - calculations.getGroundContact().depth() / groundGravityDepth) * mass / 2;
            forces.add(ForceAtPoint.atCOM(
                    new Force()
                            .setX(-groundAccelerationModule * Math.sin(groundAngle))
                            .setY(-groundAccelerationModule * Math.cos(groundAngle)),
                    FORCE_DESCRIPTION
            ));
        }
    }
}
