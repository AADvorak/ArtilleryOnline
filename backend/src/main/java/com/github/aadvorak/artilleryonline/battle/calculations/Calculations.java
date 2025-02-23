package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;

import java.util.Set;

public interface Calculations<Model> {

    Integer getId();

    Model getModel();

    VehicleCalculations getVehicleCalculations();

    Position getPosition();

    Velocity getVelocity();

    double getMass();

    void setVelocity(Velocity velocity);

    Set<Collision> getCollisions();

    void calculateNextPosition(double timeStep);

    void applyNextPosition();

    void applyNormalMoveToNextPosition(double normalMove, double angle);
}
