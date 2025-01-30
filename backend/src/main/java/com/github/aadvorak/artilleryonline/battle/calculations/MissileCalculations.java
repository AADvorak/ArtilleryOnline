package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.model.MissileModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class MissileCalculations implements Calculations<MissileModel> {

    private final MissileModel model;

    private final Set<Collision> collisions = new HashSet<>();

    private final Next next = new Next();

    private Position headPosition;

    private Position tailPosition;

    @Override
    public Integer getId() {
        return model.getId();
    }

    @Override
    public MissileModel getModel() {
        return model;
    }

    @Override
    public VehicleCalculations getVehicleCalculations() {
        return null;
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
    public void setVelocity(Velocity velocity) {
        model.getState().getVelocity()
                .setX(velocity.getX())
                .setY(velocity.getY());
    }

    public void calculateNextPosition(double timeStep) {
        var position = model.getState().getPosition();
        var velocity = model.getState().getVelocity();
        next.setPosition(position.next(velocity, timeStep));
    }

    public void applyNextPosition() {
        model.getState().setPosition(next.getPosition());
    }

    public Position getHeadPosition() {
        var length = model.getSpecs().getLength();
        var angle = model.getState().getPosition().getAngle();
        if (headPosition == null) {
            headPosition = getPosition().shifted(length / 2, angle);
        }
        return headPosition;
    }

    public Position getTailPosition() {
        var length = model.getSpecs().getLength();
        var angle = model.getState().getPosition().getAngle();
        if (tailPosition == null) {
            tailPosition = getPosition().shifted(- length / 2, angle);
        }
        return tailPosition;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class Next {

        private BodyPosition position;
    }
}
