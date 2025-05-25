package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class ShellCalculations implements Calculations<ShellModel> {

    private final ShellModel model;

    private final Set<Collision> collisions = new HashSet<>();

    private final Next next = new Next();

    private boolean hasCollisions;

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
    public void setVelocity(Velocity velocity) {
        model.getState().getVelocity()
                .setX(velocity.getX())
                .setY(velocity.getY());
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
