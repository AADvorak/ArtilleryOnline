package com.github.aadvorak.artilleryonline.battle.calculations;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class VehicleCalculations {

    private WheelCalculations rightWheel = new WheelCalculations().setSign(WheelSign.RIGHT);

    private WheelCalculations leftWheel = new WheelCalculations().setSign(WheelSign.LEFT);
}
