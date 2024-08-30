package com.github.aadvorak.artilleryonline.battle.model;

import com.github.aadvorak.artilleryonline.battle.config.ShellConfig;
import com.github.aadvorak.artilleryonline.battle.specs.ShellSpecs;
import com.github.aadvorak.artilleryonline.battle.state.ShellState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShellModel extends GenericSpecsConfigStateModel<ShellSpecs, ShellConfig, ShellState> {

    private int id;
}
