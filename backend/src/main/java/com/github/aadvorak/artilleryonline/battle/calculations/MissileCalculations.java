package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.calculator.MissileAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.VectorProjections;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.MissileModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class MissileCalculations extends CalculationsBase implements Calculations<MissileModel> {

    private final MissileModel model;

    private final Next next;

    private final Positions positions;

    private double correctingAcceleration = 0.0;

    public MissileCalculations(MissileModel model) {
        this.model = model;
        positions = new Positions(model.getSpecs().getLength());
        positions.setPosition(model.getState().getPosition());
        next = new Next(new Positions(model.getSpecs().getLength()));
    }

    @Override
    public Integer getId() {
        return model.getId();
    }

    @Override
    public MissileModel getModel() {
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
        var acceleration = MissileAccelerationCalculator.calculate(this, battleModel);
        var velocity = model.getState().getVelocity();
        var timeStep = battleModel.getCurrentTimeStepSecs();
        velocity.recalculate(acceleration, timeStep);
    }

    public void calculateNextPosition(double timeStep) {
        var position = model.getState().getPosition();
        var velocity = model.getState().getVelocity();
        next.getPositions().setPosition(position.next(velocity, timeStep));
    }

    public void applyNextPosition() {
        model.getState().setPosition(next.getPositions().getPosition());
    }

    @Override
    public void applyNormalMoveToNextPosition(double normalMove, double angle) {
        var move = new VectorProjections(angle).setNormal(normalMove).recoverPosition();
        var nextPosition = next.getPositions().getPosition();
        nextPosition.setX(nextPosition.getX() + move.getX());
        nextPosition.setY(nextPosition.getY() + move.getY());
        next.getPositions().setPosition(nextPosition);
    }

    @Getter
    @RequiredArgsConstructor
    public static final class Next {
        private final Positions positions;
    }

    @RequiredArgsConstructor
    public static final class Positions {

        private final double length;

        @Getter
        private BodyPosition position;

        private Position head;

        private Position tail;

        public Position getHead() {
            if (head == null) {
                head = position.getCenter().shifted(length / 2, position.getAngle());
            }
            return head;
        }

        public Position getTail() {
            if (tail == null) {
                tail = position.getCenter().shifted(- length / 2, position.getAngle());
            }
            return tail;
        }

        public Position getCenter() {
            return position.getCenter();
        }

        public void setPosition(BodyPosition position) {
            this.position = position;
            this.head = null;
            this.tail = null;
        }
    }
}
