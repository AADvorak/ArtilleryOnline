package com.github.aadvorak.artilleryonline.battle.model;

import com.github.aadvorak.artilleryonline.battle.config.ExplosionConfig;
import com.github.aadvorak.artilleryonline.battle.specs.ExplosionSpecs;
import com.github.aadvorak.artilleryonline.battle.state.ExplosionState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExplosionModel extends GenericSpecsConfigStateModel<ExplosionSpecs, ExplosionConfig, ExplosionState> {

    private int id;
}
