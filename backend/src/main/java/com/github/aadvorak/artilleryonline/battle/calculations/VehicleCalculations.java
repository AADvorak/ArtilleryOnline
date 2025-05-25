package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class VehicleCalculations
        implements BodyCalculations<VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel> {

    private final VehicleModel model;

    private final WheelCalculations rightWheel;

    private final WheelCalculations leftWheel;

    private Set<Contact> groundContacts;

    private BodyPosition nextPosition;

    private Set<Collision> collisions = new HashSet<>();

    private boolean hasCollisions = false;

    public VehicleCalculations(VehicleModel model) {
        this.model = model;
        rightWheel = new WheelCalculations(WheelSign.RIGHT, this);
        leftWheel = new WheelCalculations(WheelSign.LEFT, this);
    }

    @Override
    public Integer getId() {
        return model.getId();
    }

    @Override
    public Position getPosition() {
        return model.getState().getPosition().getCenter();
    }

    public Position getGeometryPosition() {
        return getPosition().shifted(model.getPreCalc().getCenterOfMassShift()
                .plusAngle(model.getState().getPosition().getAngle()).inverted());
    }

    public BodyPosition getGeometryNextPosition() {
        return nextPosition.shifted(model.getPreCalc().getCenterOfMassShift()
                .plusAngle(nextPosition.getAngle()).inverted());
    }

    @Override
    public Velocity getVelocity() {
        return model.getState().getVelocity().getMovingVelocity();
    }

    @Override
    public double getMass() {
        return model.getPreCalc().getMass();
    }

    @Override
    public void setVelocity(Velocity velocity) {
        model.getState().getVelocity()
                .setX(velocity.getX())
                .setY(velocity.getY());
    }

    public void recalculateWheelsVelocities() {
        rightWheel.calculateVelocity();
        leftWheel.calculateVelocity();
    }

    public void recalculateVelocityByWheel(WheelCalculations wheel) {
        var rightWheelVelocity = rightWheel.getVelocity();
        var leftWheelVelocity = leftWheel.getVelocity();
        var angle = model.getState().getPosition().getAngle();
        var angleVelocity = Math.abs(angle) < Math.PI / 4
                ? (rightWheelVelocity.getY() - leftWheelVelocity.getY()) / (2.0 * Math.cos(angle))
                : (leftWheelVelocity.getX() - rightWheelVelocity.getX()) / (2.0 * Math.sin(angle));
        var wheelSign = wheel.getSign().getValue();
        model.getState().getVelocity()
                .setAngle(angleVelocity / model.getPreCalc().getWheelDistance())
                .setX(wheel.getVelocity().getX() - wheelSign * angleVelocity * Math.sin(angle))
                .setY(wheel.getVelocity().getY() + wheelSign * angleVelocity * Math.cos(angle));
        recalculateWheelsVelocities();
    }

    public void applyNextPosition() {
        model.getState().setPosition(nextPosition);
    }

    public void applyNormalMoveToNextPosition(double normalMove, double angle) {
        var move = new VectorProjections(angle).setNormal(normalMove).recoverPosition();
        nextPosition.setX(nextPosition.getX() + move.getX());
        nextPosition.setY(nextPosition.getY() + move.getY());
    }

    public void calculateNextPosition(double timeStep) {
        var position = model.getState().getPosition();
        var velocity = model.getState().getVelocity();
        setNextPosition(position.next(velocity, timeStep));
        rightWheel.calculateNextPosition();
        leftWheel.calculateNextPosition();
    }
}
