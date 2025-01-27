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
        next.setPosition(new BodyPosition()
                .setX(position.getX() + velocity.getX() * timeStep)
                .setY(position.getY() + velocity.getY() * timeStep)
                .setAngle(position.getAngle() + velocity.getAngle() * timeStep)
        );
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class Next {

        private BodyPosition position;
    }
}
