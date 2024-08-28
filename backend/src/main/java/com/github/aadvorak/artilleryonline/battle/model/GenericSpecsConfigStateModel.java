package com.github.aadvorak.artilleryonline.battle.model;

import com.github.aadvorak.artilleryonline.battle.config.Config;
import com.github.aadvorak.artilleryonline.battle.specs.Specs;
import com.github.aadvorak.artilleryonline.battle.state.State;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericSpecsConfigStateModel<S extends Specs, C extends Config, St extends State> {

    private S specs;

    private C config;

    private St state;
}
