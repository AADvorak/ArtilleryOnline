package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.model.DroneModel;
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
public class DroneCalculations implements Calculations<DroneModel> {

    private final DroneModel model;

    private final Set<Collision> collisions = new HashSet<>();

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

        private Position nearestGroundPointByX;

        private NearestGroundPoint nearestGroundPoint;

        private double groundAngle;

        private double groundDepth;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class Target {

        private double xDiff;

        private double angleDiff;
    }
}
