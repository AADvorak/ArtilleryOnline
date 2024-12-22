package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Acceleration;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.CollideObject;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class VehicleCalculations implements Calculations {

    private VehicleModel model;

    private Position nextPosition;

    private double nextAngle;

    private Set<CollideObject> collisions = new HashSet<>();

    private Set<Calculations> vehicleCollisions = new HashSet<>();

    private Acceleration vehicleElasticityAcceleration = new Acceleration();

    private Acceleration wallElasticityAcceleration = new Acceleration();

    private Acceleration sumElasticityAcceleration;

    private WheelCalculations rightWheel = new WheelCalculations().setSign(WheelSign.RIGHT);

    private WheelCalculations leftWheel = new WheelCalculations().setSign(WheelSign.LEFT);

    @Override
    public Position getPosition() {
        return model.getState().getPosition();
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
}
