package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.BodyCalculations;
import com.github.aadvorak.artilleryonline.battle.common.BodyVelocity;
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
        var preCalc = calculations.getModel().getPreCalc();
        velocity.recalculate(acceleration, timeStep);
        fade(velocity, battleModel.getRoom().getSpecs().getAirFrictionCoefficient(), timeStep, preCalc);
        if (Math.abs(velocity.getX()) < Constants.MIN_VELOCITY) {
            velocity.setX(0.0);
        } else if (Math.abs(velocity.getX()) > Constants.MAX_VELOCITY) {
            velocity.setX(Constants.MAX_VELOCITY * Math.signum(velocity.getX()));
        }
        if (Math.abs(velocity.getY()) < Constants.MIN_VELOCITY) {
            velocity.setY(0.0);
        } else if (Math.abs(velocity.getY()) > Constants.MAX_VELOCITY) {
            velocity.setY(Constants.MAX_VELOCITY * Math.signum(velocity.getY()));
        }
        if (Math.abs(velocity.getAngle() * preCalc.getMaxRadius()) < Constants.MIN_VELOCITY) {
            velocity.setAngle(0.0);
        } else if (Math.abs(velocity.getAngle()) > Constants.MAX_VELOCITY) {
            velocity.setAngle(Constants.MAX_VELOCITY * Math.signum(velocity.getAngle()));
        }
    }

    private void fade(BodyVelocity velocity, double frictionCoefficient, double timeStep, BodyPreCalc preCalc) {
        var movingCoefficient = 2 * frictionCoefficient * preCalc.getMaxRadius() / preCalc.getMass();
        var rotatingCoefficient = 2 * Math.PI * frictionCoefficient * Math.pow(preCalc.getMaxRadius(), 2)
                / preCalc.getMomentOfInertia();
        velocity.setX(velocity.getX() / (1 + movingCoefficient * Math.abs(velocity.getX()) * timeStep));
        velocity.setY(velocity.getY() / (1 + movingCoefficient * Math.abs(velocity.getY()) * timeStep));
        velocity.setAngle(velocity.getAngle() / (1 + rotatingCoefficient * Math.abs(velocity.getAngle()) * timeStep));
    }
}
