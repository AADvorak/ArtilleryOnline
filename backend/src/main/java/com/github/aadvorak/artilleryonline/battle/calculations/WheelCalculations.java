package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Acceleration;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class WheelCalculations {

    private WheelSign sign;

    private Position position;

    private Velocity velocity = new Velocity();

    private Position nearestGroundPointByX;

    private PositionAndDistance nearestGroundPoint;

    private double groundAngle;

    private double depth;

    private Acceleration gravityAcceleration = new Acceleration();

    private Acceleration groundReactionAcceleration = new Acceleration();

    private Acceleration groundFrictionAcceleration = new Acceleration();

    private Acceleration engineAcceleration = new Acceleration();

    private Acceleration sumAcceleration;

    public Acceleration getSumAcceleration() {
        if (sumAcceleration == null) {
            sumAcceleration = Acceleration.sumOf(
                    gravityAcceleration,
                    groundReactionAcceleration,
                    groundFrictionAcceleration,
                    engineAcceleration
            );
        }
        return sumAcceleration;
    }
}
