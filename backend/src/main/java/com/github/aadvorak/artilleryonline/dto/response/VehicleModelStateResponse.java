package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.state.VehicleState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleModelStateResponse {

    private int id;

    private VehicleState state;
}
