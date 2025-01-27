package com.github.aadvorak.artilleryonline.battle.model;

import com.github.aadvorak.artilleryonline.battle.config.MissileConfig;
import com.github.aadvorak.artilleryonline.battle.specs.MissileSpecs;
import com.github.aadvorak.artilleryonline.battle.state.MissileState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissileModel extends GenericSpecsConfigStateModel<MissileSpecs, MissileConfig, MissileState> {

    private int id;

    private int vehicleId;

    private Long userId;
}
