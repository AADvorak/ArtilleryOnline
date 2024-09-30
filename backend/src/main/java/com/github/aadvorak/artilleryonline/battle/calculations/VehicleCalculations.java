package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class VehicleCalculations {

    private Position nextPosition;

    private double nextAngle;

    private WheelCalculations rightWheel = new WheelCalculations().setSign(WheelSign.RIGHT);

    private WheelCalculations leftWheel = new WheelCalculations().setSign(WheelSign.LEFT);
}
