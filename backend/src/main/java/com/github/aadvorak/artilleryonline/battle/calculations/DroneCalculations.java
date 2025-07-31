package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.calculator.DroneAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.VectorProjections;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.config.DroneConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.DroneModel;
import com.github.aadvorak.artilleryonline.battle.precalc.DronePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.DroneSpecs;
import com.github.aadvorak.artilleryonline.battle.state.DroneState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@RequiredArgsConstructor
public class DroneCalculations extends CalculationsBase
        implements BodyCalculations<DroneSpecs, DronePreCalc, DroneConfig, DroneState, DroneModel> {

    private final DroneModel model;

    private final Next next = new Next();

    private Target target;

    private double height;

    @Override
    public Integer getId() {
        return model.getId();
    }

    @Override
    public DroneModel getModel() {
        return model;
    }

    @Override
    public Position getPosition() {
        return model.getState().getPosition().getCenter();
    }

    @Override
    public Velocity getVelocity() {
        return model.getState().getVelocity().getMovingVelocity();
    }

    @Override
    public double getMass() {
        return model.getSpecs().getMass();
    }

    @Override
    public void recalculateVelocity(BattleModel battleModel) {
        var acceleration = DroneAccelerationCalculator.calculate(this, battleModel);
        var velocity = model.getState().getVelocity();
        var timeStep = battleModel.getCurrentTimeStepSecs();
        velocity.recalculate(acceleration, timeStep);
    }

    public void calculateNextPosition(double timeStep) {
        var position = model.getState().getPosition();
        var velocity = model.getState().getVelocity();
        next.setPosition(position.next(velocity, timeStep));
    }

    public void applyNextPosition() {
        model.getState().setPosition(next.getPosition());
    }

    public void applyNormalMoveToNextPosition(double normalMove, double angle) {
        var move = new VectorProjections(angle).setNormal(normalMove).recoverPosition();
        var nextPosition = next.getPosition();
        nextPosition.setX(nextPosition.getX() + move.getX());
        nextPosition.setY(nextPosition.getY() + move.getY());
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class Next {
        private BodyPosition position;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class Target {

        private double xDiff;

        private double angleDiff;
    }
}
