package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.battle.specs.ShellSpecs;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GunState implements State {

    private ShellSpecs loadedShell;

    private ShellSpecs selectedShell;

    private ShellSpecs loadingShell;

    private double loadRemainTime;

    private boolean triggerPushed;
}
