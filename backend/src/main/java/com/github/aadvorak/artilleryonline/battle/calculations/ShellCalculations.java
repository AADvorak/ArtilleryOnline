package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@RequiredArgsConstructor
public class ShellCalculations extends CalculationsBase implements Calculations<ShellModel> {

    private final ShellModel model;

    private final Next next = new Next();

    @Override
    public Integer getId() {
        return model.getId();
    }

    @Override
    public ShellModel getModel() {
        return model;
    }

    @Override
    public Position getPosition() {
        return model.getState().getPosition();
    }

    @Override
    public Velocity getVelocity() {
        return model.getState().getVelocity();
    }

    @Override
    public double getMass() {
        return model.getSpecs().getMass();
    }

    @Override
    public void recalculateVelocity(BattleModel battleModel) {
        if (model.getState().isStuck()) {
            return;
        }
        var velocity = getVelocity();
        // todo collision
        if (BattleUtils.positionIsOutOfRoom(getPosition(), battleModel.getRoom().getSpecs())) {
            velocity.setX(-velocity.getX());
        }
        var gravityAcceleration = battleModel.getRoom().getSpecs().getGravityAcceleration();
        velocity.setY(velocity.getY() - 2.25 * gravityAcceleration * battleModel.getCurrentTimeStepSecs());
    }

    public void calculateNextPosition(double timeStep) {
        var position = getPosition();
        var velocity = getVelocity();
        next.setPosition(new Position()
                .setX(position.getX() + velocity.getX() * timeStep)
                .setY(position.getY() + velocity.getY() * timeStep));
    }

    @Override
    public void applyNextPosition() {
        model.getState().setPosition(next.getPosition());
    }

    @Override
    public void applyNormalMoveToNextPosition(double normalMove, double angle) {

    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class Next {

        private Position position;
    }
}
