package com.github.aadvorak.artilleryonline.battle.calculations;

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
public class VehicleCalculations {

    private VehicleModel model;

    private Position nextPosition;

    private double nextAngle;

    private Set<CollideObject> collisions = new HashSet<>();

    private WheelCalculations rightWheel = new WheelCalculations().setSign(WheelSign.RIGHT);

    private WheelCalculations leftWheel = new WheelCalculations().setSign(WheelSign.LEFT);
}
