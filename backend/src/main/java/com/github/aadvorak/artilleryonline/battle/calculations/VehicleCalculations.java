package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Acceleration;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.VectorUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class VehicleCalculations implements Calculations {

    private final VehicleModel model;

    private final WheelCalculations rightWheel;

    private final WheelCalculations leftWheel;

    private Position nextPosition;

    private double nextAngle;

    private Set<Collision> collisions = new HashSet<>();

    private Set<Calculations> vehicleCollisions = new HashSet<>();

    private Acceleration vehicleElasticityAcceleration = new Acceleration();

    private Acceleration wallElasticityAcceleration = new Acceleration();

    private Acceleration sumElasticityAcceleration;

    private boolean hasCollisions = false;

    public VehicleCalculations(VehicleModel model) {
        this.model = model;
        rightWheel = new WheelCalculations(WheelSign.RIGHT, this);
        leftWheel = new WheelCalculations(WheelSign.LEFT, this);
    }

    @Override
    public Integer getVehicleId() {
        return model.getId();
    }

    @Override
    public VehicleCalculations getVehicleCalculations() {
        return this;
    }

    @Override
    public Position getPosition() {
        return model.getState().getPosition();
    }

    @Override
    public Velocity getVelocity() {
        return model.getState().getVelocity().getMovingVelocity();
    }

    @Override
    public void setVelocity(Velocity velocity) {
        model.getState().getVelocity()
                .setX(velocity.getX())
                .setY(velocity.getY());
    }

    public Acceleration getSumElasticityAcceleration() {
        if (sumElasticityAcceleration == null) {
            sumElasticityAcceleration = Acceleration.sumOf(
                    vehicleElasticityAcceleration,
                    wallElasticityAcceleration
            );
        }
        return sumElasticityAcceleration;
    }

    public void recalculateWheelsVelocities() {
        rightWheel.calculateVelocity();
        leftWheel.calculateVelocity();
    }

    public void recalculateVelocityByWheel(WheelCalculations wheel) {
        var rightWheelVelocity = rightWheel.getVelocity();
        var leftWheelVelocity = leftWheel.getVelocity();
        var angle = model.getState().getAngle();

        var angleVelocity = Math.abs(angle) < Math.PI / 4
                ? (rightWheelVelocity.getY() - leftWheelVelocity.getY()) / (2.0 * Math.cos(angle))
                : (leftWheelVelocity.getX() - rightWheelVelocity.getX()) / (2.0 * Math.sin(angle));
        var wheelSign = wheel.getSign().getValue();
        model.getState().getVelocity()
                .setAngle(angleVelocity)
                .setX(wheel.getVelocity().getX() - wheelSign * angleVelocity * Math.sin(angle))
                .setY(wheel.getVelocity().getY() + wheelSign * angleVelocity * Math.cos(angle));
    }

    public void applyNextPositionAndAngle() {
        model.getState().setPosition(
                new Position()
                        .setX(nextPosition.getX())
                        .setY(nextPosition.getY())
        );
        model.getState().setAngle(nextAngle);
    }

    public void applyNormalMoveToNextPosition(double normalMove, double angle) {
        var tMove = 0.0;
        var xMove = VectorUtils.getComponentX(normalMove, tMove, angle);
        var yMove = VectorUtils.getComponentY(normalMove, tMove, angle);
        nextPosition.setX(nextPosition.getX() + xMove);
        nextPosition.setY(nextPosition.getY() + yMove);
    }

    public void calculateNextPositionAndAngle(double timeStep) {
        setNextPosition(
                new Position()
                        .setX(model.getState().getPosition().getX())
                        .setY(model.getState().getPosition().getY())
        );
        setNextAngle(model.getState().getAngle());
        moveNextPositionAndAngle(timeStep);
    }

    public void moveNextPositionAndAngle(double timeStep) {
        var vehicleVelocity = model.getState().getVelocity();
        var positionShift = new Position()
                .setX(vehicleVelocity.getX() * timeStep)
                .setY(vehicleVelocity.getY() * timeStep);
        addToNextPositionAndAngle(positionShift, vehicleVelocity.getAngle() * timeStep);
    }

    private void addToNextPositionAndAngle(Position positionShift, double angleShift) {
        nextPosition
                .setX(nextPosition.getX() + positionShift.getX())
                .setY(nextPosition.getY() + positionShift.getY());
        var nextAngle = this.nextAngle + angleShift;
        if (nextAngle > Math.PI / 2) {
            nextAngle = Math.PI / 2;
        }
        if (nextAngle < -Math.PI / 2) {
            nextAngle = -Math.PI / 2;
        }
        this.nextAngle = nextAngle;
        rightWheel.calculateNextPosition();
        leftWheel.calculateNextPosition();
    }
}
