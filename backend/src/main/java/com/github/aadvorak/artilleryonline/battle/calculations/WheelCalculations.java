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
import lombok.experimental.Accessors;

import java.util.Set;

@Getter
@Setter
public class WheelCalculations
        implements BodyCalculations<VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel> {

    private final WheelSign sign;

    private final VehicleCalculations vehicle;

    private Position position;

    private Velocity velocity;

    private Contact groundContact;

    private final Next next = new Next();

    public WheelCalculations(WheelSign sign, VehicleCalculations vehicle) {
        this.sign = sign;
        this.vehicle = vehicle;
        this.calculatePosition();
    }

    @Override
    public Integer getId() {
        return vehicle.getModel().getId();
    }

    @Override
    public VehicleModel getModel() {
        return vehicle.getModel();
    }

    @Override
    public double getMass() {
        return vehicle.getMass();
    }

    @Override
    public Set<Collision> getCollisions() {
        return vehicle.getCollisions();
    }

    @Override
    public boolean isHasCollisions() {
        return vehicle.isHasCollisions();
    }

    @Override
    public void setHasCollisions(boolean hasCollisions) {
        vehicle.setHasCollisions(hasCollisions);
    }

    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
        vehicle.recalculateVelocityByWheel(this);
    }

    @Override
    public void calculateNextPosition(double timeStep) {
        vehicle.calculateNextPosition(timeStep);
    }

    @Override
    public void applyNextPosition() {
        vehicle.applyNextPosition();
    }

    @Override
    public void applyNormalMoveToNextPosition(double normalMove, double angle) {
        vehicle.applyNormalMoveToNextPosition(normalMove, angle);
    }

    public void calculateVelocity() {
        var vehicleVelocity = vehicle.getModel().getState().getVelocity();
        var angle = vehicle.getModel().getState().getPosition().getAngle();
        var wheelAngle = angle + Math.PI / 2 + sign.getValue() * vehicle.getModel().getPreCalc().getWheelAngle();
        var wheelDistance = vehicle.getModel().getPreCalc().getWheelDistance();
        var wheelVelocity = vehicleVelocity.getPointVelocity(wheelDistance, wheelAngle);
        if (velocity == null) {
            velocity = new Velocity();
        }
        velocity
                .setX(wheelVelocity.getX())
                .setY(wheelVelocity.getY());
    }

    public void calculatePosition() {
        setPosition(calculatePosition(vehicle.getModel().getState().getPosition()));
    }

    public void calculateNextPosition() {
        next.setPosition(calculatePosition(vehicle.getNextPosition()));
    }

    private Position calculatePosition(BodyPosition vehiclePosition) {
        var wheelDistance = vehicle.getModel().getPreCalc().getWheelDistance();
        var wheelAngle = vehicle.getModel().getPreCalc().getWheelAngle();
        return new Position()
                .setX(vehiclePosition.getX() - sign.getValue() * wheelDistance * Math.cos(vehiclePosition.getAngle()
                        + sign.getValue() * wheelAngle))
                .setY(vehiclePosition.getY() - sign.getValue() * wheelDistance * Math.sin(vehiclePosition.getAngle()
                        + sign.getValue() * wheelAngle));
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class Next {
        private Position position;
    }
}
