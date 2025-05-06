package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.config.DroneConfig;
import com.github.aadvorak.artilleryonline.battle.model.DroneModel;
import com.github.aadvorak.artilleryonline.battle.precalc.DronePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.DroneSpecs;
import com.github.aadvorak.artilleryonline.battle.state.DroneState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class DroneCalculations
        implements BodyCalculations<DroneSpecs, DronePreCalc, DroneConfig, DroneState, DroneModel> {

    private final DroneModel model;

    private final Set<Collision> collisions = new HashSet<>();

    private final Next next = new Next();

    private Target target;

    private double height;

    private boolean hasCollisions;

    @Override
    public Integer getId() {
        return model.getId();
    }

    @Override
    public DroneModel getModel() {
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
