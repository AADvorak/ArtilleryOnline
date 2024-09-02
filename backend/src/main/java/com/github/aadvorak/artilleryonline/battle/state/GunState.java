package com.github.aadvorak.artilleryonline.battle.state;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GunState implements State {

    private String loadedShell;

    private String selectedShell;

    private String loadingShell;

    private double loadRemainTime;

    private boolean triggerPushed;
}
