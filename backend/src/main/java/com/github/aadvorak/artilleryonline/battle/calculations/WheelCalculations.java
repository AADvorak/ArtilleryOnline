package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Getter
@Setter
public class WheelCalculations implements Calculations<VehicleModel> {

    private final WheelSign sign;

    private final VehicleCalculations vehicle;

    private WheelGroundState groundState;

    private Position position;

    private Velocity velocity;

    private Position nearestGroundPointByX;

    private NearestGroundPoint nearestGroundPoint;

    private double groundAngle;

    private double groundDepth;

    private Acceleration gravityAcceleration = new Acceleration();

    private Acceleration groundReactionAcceleration = new Acceleration();

    private Acceleration groundFrictionAcceleration = new Acceleration();

    private Acceleration engineAcceleration = new Acceleration();

    private Acceleration jetAcceleration = new Acceleration();

    private Acceleration sumAcceleration;

    private final Next next = new Next();

    public WheelCalculations(WheelSign sign, VehicleCalculations vehicle) {
        this.sign = sign;
        this.vehicle = vehicle;
        this.calculatePosition();
    }

    public Acceleration getSumAcceleration() {
        if (sumAcceleration == null) {
            sumAcceleration = Acceleration.sumOf(
                    gravityAcceleration,
                    groundReactionAcceleration,
                    groundFrictionAcceleration,
                    engineAcceleration,
                    jetAcceleration
            );
        }
        return sumAcceleration;
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
    public VehicleCalculations getVehicleCalculations() {
        return vehicle;
    }

    @Override
    public double getMass() {
        return vehicle.getMass();
    }

    @Override
    public Set<Collision> getCollisions() {
        return vehicle.getCollisions();
    }

    public void calculateVelocity() {
        var vehicleVelocity = vehicle.getModel().getState().getVelocity();
        var angle = vehicle.getModel().getState().getPosition().getAngle();
        var wheelAngle = angle + Math.PI / 2 + sign.getValue() * Math.PI / 2;
        var wheelDistance = vehicle.getModel().getPreCalc().getWheelDistance();
        setVelocity(vehicleVelocity.getPointVelocity(wheelDistance, wheelAngle));
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

        private Position nearestGroundPointByX;

        private NearestGroundPoint nearestGroundPoint;

        private double groundAngle;

        private double groundDepth;
    }
}
