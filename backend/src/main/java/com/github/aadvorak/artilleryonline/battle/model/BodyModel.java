package com.github.aadvorak.artilleryonline.battle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.aadvorak.artilleryonline.battle.config.Config;
import com.github.aadvorak.artilleryonline.battle.precalc.BodyPreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.Specs;
import com.github.aadvorak.artilleryonline.battle.state.BodyState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BodyModel<S extends Specs,  P extends BodyPreCalc, C extends Config, St extends BodyState>
        extends GenericSpecsConfigStateModel<S, C, St> {

    private P preCalc;

    @JsonIgnore
    private final TimeoutUpdate update = new TimeoutUpdate();
}
