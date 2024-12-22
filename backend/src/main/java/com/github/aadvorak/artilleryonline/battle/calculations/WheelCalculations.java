package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Acceleration;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class WheelCalculations implements Calculations {

    private WheelSign sign;

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
}
