package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Acceleration;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class WheelCalculations implements Calculations {

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

    private Acceleration vehicleElasticityAcceleration = new Acceleration();

    private Acceleration groundElasticityAcceleration = new Acceleration();

    private Acceleration sumElasticityAcceleration;

    private Set<Calculations> vehicleCollisions = new HashSet<>();

    private Next next = new Next();

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

    public Acceleration getSumElasticityAcceleration() {
        if (sumElasticityAcceleration == null) {
            sumElasticityAcceleration = Acceleration.sumOf(
                    vehicleElasticityAcceleration,
                    groundElasticityAcceleration
            );
        }
        return sumElasticityAcceleration;
    }

    @Override
    public Integer getVehicleId() {
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
    public Set<Collision> getCollisions() {
        return vehicle.getCollisions();
    }

    public void calculateVelocity() {
        var vehicleVelocity = vehicle.getModel().getState().getVelocity();
        var angle = vehicle.getModel().getState().getAngle();
        var angleVelocity = vehicleVelocity.getAngle() * vehicle.getModel().getSpecs().getRadius();
        var velocityX = vehicleVelocity.getX() + sign.getValue() * angleVelocity * Math.sin(angle);
        var velocityY = vehicleVelocity.getY() - sign.getValue() * angleVelocity * Math.cos(angle);
        setVelocity(new Velocity()
                .setX(velocityX)
                .setY(velocityY));
    }

    public void calculatePosition() {
        setPosition(calculatePosition(vehicle.getPosition(), vehicle.getModel().getState().getAngle()));
    }

    public void calculateNextPosition() {
        next.setPosition(calculatePosition(vehicle.getNextPosition(), vehicle.getNextAngle()));
    }

    private Position calculatePosition(Position vehiclePosition, double vehicleAngle) {
        var wheelDistance = vehicle.getModel().getPreCalc().getWheelDistance();
        var wheelAngle = vehicle.getModel().getPreCalc().getWheelAngle();
        return new Position()
                .setX(vehiclePosition.getX()
                        - sign.getValue() * wheelDistance * Math.cos(vehicleAngle + sign.getValue() * wheelAngle))
                .setY(vehiclePosition.getY()
                        - sign.getValue() * wheelDistance * Math.sin(vehicleAngle + sign.getValue() * wheelAngle));
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class Next {

        private Position position;

        private Position nearestGroundPointByX;
    }
}
