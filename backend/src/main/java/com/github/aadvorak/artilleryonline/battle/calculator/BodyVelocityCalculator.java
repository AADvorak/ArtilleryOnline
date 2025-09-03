package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.BodyCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Constants;
import com.github.aadvorak.artilleryonline.battle.config.Config;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.BodyModel;
import com.github.aadvorak.artilleryonline.battle.precalc.BodyPreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.Specs;
import com.github.aadvorak.artilleryonline.battle.state.BodyState;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BodyVelocityCalculator<
        S extends Specs,
        P extends BodyPreCalc,
        Cf extends Config,
        St extends BodyState,
        M extends BodyModel<S, P, Cf, St>,
        C extends BodyCalculations<S, P, Cf, St, M>> {

    private final BodyAccelerationCalculator<S, P, Cf, St, M, C> accelerationCalculator;

    public void recalculateVelocity(C calculations, BattleModel battleModel) {
        calculations.calculateAllGroundContacts(battleModel.getRoom());
        var acceleration = accelerationCalculator.calculate(calculations, battleModel);
        var velocity = calculations.getModel().getState().getVelocity();
        var timeStep = battleModel.getCurrentTimeStepSecs();
        var maxRadius = calculations.getModel().getPreCalc().getMaxRadius();
        velocity.recalculate(acceleration, timeStep);
        if (Math.abs(velocity.getX()) < Constants.MIN_VELOCITY) {
            velocity.setX(0.0);
        }
        if (Math.abs(velocity.getY()) < Constants.MIN_VELOCITY) {
            velocity.setY(0.0);
        }
        if (Math.abs(velocity.getAngle() * maxRadius) < Constants.MIN_VELOCITY) {
            velocity.setAngle(0.0);
        }
    }
}
