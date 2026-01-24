package com.github.aadvorak.artilleryonline.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleParticipantParams {

    private String selectedVehicle;

    private String vehicleColor;
}
