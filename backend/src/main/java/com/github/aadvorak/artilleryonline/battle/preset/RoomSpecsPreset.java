package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.specs.RoomSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomSpecsPreset {

    DEFAULT("default", new RoomSpecs()
            .setGravityAcceleration(9.8)
            .setGroundMaxDepth(0.04)
            .setSurfaceMaxDepth(0.01)
            .setGroundReactionCoefficient(25.4)
            .setGroundFrictionCoefficient(1.8)
            .setAirFrictionCoefficient(0.1)
            .setStep(0.01)
            .setLeftBottom(new Position().setX(0.0).setY(0.0))
            .setRightTop(new Position().setX(30.0).setY(13.5))),

    NO_GRAVITY("noGravity", new RoomSpecs()
            .setGravityAcceleration(0.0)
            .setGroundMaxDepth(0.04)
            .setSurfaceMaxDepth(0.01)
            .setGroundReactionCoefficient(25.4)
            .setGroundFrictionCoefficient(1.8)
            .setAirFrictionCoefficient(0.1)
            .setStep(0.01)
            .setLeftBottom(new Position().setX(0.0).setY(0.0))
            .setRightTop(new Position().setX(20.0).setY(9.0)));

    private final String name;

    private final RoomSpecs specs;
}
