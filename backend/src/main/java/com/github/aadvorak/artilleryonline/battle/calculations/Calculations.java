package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

import java.util.Set;

public interface Calculations<Model> {

    Integer getId();

    Model getModel();

    Position getPosition();

    Velocity getVelocity();

    double getMass();

    Set<Collision> getLastCollisions();

    Set<Collision> getAllCollisions();

    Set<Collision> getCollisions(int iterationNumber);

    boolean isHasCollisions();

    void setHasCollisions(boolean hasCollisions);

    void recalculateVelocity(BattleModel battleModel);

    void calculateNextPosition(double timeStep);

    void applyNextPosition();

    void applyNormalMoveToNextPosition(double normalMove, double angle);
}
