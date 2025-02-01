package com.github.aadvorak.artilleryonline.battle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleModel extends GenericSpecsConfigStateModel<VehicleSpecs, VehicleConfig, VehicleState> {

    private int id;

    @JsonIgnore
    private Long userId;

    private VehiclePreCalc preCalc;

    @JsonIgnore
    private boolean updated = false;
}
