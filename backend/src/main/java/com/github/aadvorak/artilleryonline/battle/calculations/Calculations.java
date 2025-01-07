package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Acceleration;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

import java.util.Set;

public interface Calculations {

    Integer getVehicleId();

    VehicleModel getModel();

    VehicleCalculations getVehicleCalculations();

    Position getPosition();

    Velocity getVelocity();

    void setVelocity(Velocity velocity);

    Set<Collision> getCollisions();

    Set<Calculations> getVehicleCollisions();

    Acceleration getVehicleElasticityAcceleration();

    default void addVehicleCollision(Calculations vehicleCollision) {
        getVehicleCollisions().add(vehicleCollision);
    }

    default boolean collisionNotCalculated(Calculations calculation) {
        return !getVehicleCollisions().contains(calculation);
    }
}
