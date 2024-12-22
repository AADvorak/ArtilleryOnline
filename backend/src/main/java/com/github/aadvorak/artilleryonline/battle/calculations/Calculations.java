package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Acceleration;
import com.github.aadvorak.artilleryonline.battle.common.Position;

import java.util.Set;

public interface Calculations {

    Position getPosition();

    Set<Calculations> getVehicleCollisions();

    Acceleration getVehicleElasticityAcceleration();

    default void addVehicleCollision(Calculations vehicleCollision) {
        getVehicleCollisions().add(vehicleCollision);
    }

    default boolean collisionNotCalculated(Calculations calculation) {
        return !getVehicleCollisions().contains(calculation);
    }
}
