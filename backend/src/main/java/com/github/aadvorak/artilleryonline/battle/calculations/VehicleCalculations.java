package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Acceleration;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;
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
        VehicleUtils.calculateWheelVelocity(model, rightWheel);
        VehicleUtils.calculateWheelVelocity(model, leftWheel);
    }

    public void recalculateVelocityByWheel(WheelCalculations wheel) {
        VehicleUtils.recalculateVehicleVelocityByWheel(this, wheel);
    }
}
